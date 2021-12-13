package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.Set;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Cluster cluster;
    private Model model; // current model gpu is working on
    private int capacity; // amount of batches could be stored at the same time
    private volatile int curCapacity; // amount of possible databatches to send/receive
    private long time; // current tick
    private int tick; // num of ticks to train  process data
    private LinkedList<DataBatch> preTrained; // databatch that has been processed by a cpu
    private LinkedList<DataBatch> preProcessed; // databatch that is needed to be processed by a cpu
    private boolean isDone; // does the gpu finished training current model



    public GPU (Type type){
        this.type = type;
        cluster = Cluster.getInstance();
        time = 1;
        switch (type){
            case RTX2080:
                capacity = 16;
                tick = 2;
                break;
            case RTX3090:
                capacity = 32;
                tick = 1;
                break;
            case GTX1080:
                capacity = 8;
                tick = 4;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " +type);
        }
        curCapacity = capacity;
        isDone = false;
    }
    /**
     * @pre preTrained.isEmpty()
     * @post this.model = model && batchIdx == 0
     *
     *
     * @param model
     */
    public void setModel(Model model) throws InterruptedException {
        this.model = model;
        curCapacity = capacity;
        splitData();
        sendToCluster();
        isDone = false;
    }
    public void updateTime(){
        time = time + 1;
        notifyAll();
    }

    private void splitData(){
        Data data = model.getData();
        int currentSize = 0 ;
        while(currentSize < data.getSize()){
            synchronized (preProcessed){
                preProcessed.addLast(new DataBatch(data,currentSize));
            }
            currentSize = currentSize + 1000;
        }
    }

    /**
     *send data to be processed only if there is enough space to receive it back
     */
    private void sendToCluster() throws InterruptedException {
        while (!preProcessed.isEmpty()){
            while ( curCapacity < 0)
                wait();
            curCapacity = curCapacity -1;
            synchronized (preTrained) {
                cluster.receiveProcessed(preTrained.removeFirst());
            }
        }
    }


    /**
     * get processed data from cluster and insert it into preTrained collection
     * @pre preTrained.size < capacity && processed.data == this.model.data
     * @inv preTrained.size <= capacity
     * @post preTrained.size <= capacity && @pre(preTrained.size) + 1 == preTrained.size
     *
     * @param processed
     */
    public void insertProcessed(DataBatch processed) throws InterruptedException {
        if(curCapacity < 0)
            throw new IllegalStateException(" cannot except new processed databatches");
        curCapacity = curCapacity +1;
        synchronized (preTrained){
            preTrained.addLast(processed);
        }
        train();
    }



    /**
     *  number of ticks required deteremined by GPU type
     *  when finished training all data, need to finish event
     */
    private void train() throws InterruptedException {
        while(preTrained.isEmpty())
            wait();
        long currentTime = time;
        while(time - currentTime < tick)
            wait();
        model.getData().updateProcess(1000);
        curCapacity = curCapacity - 1;
        if(model.getData().getSize() == model.getData().getProcessed()) {
            isDone = true;
        }

    }
    public boolean isDone(){ return isDone;}
    public int getCapacity(){return capacity;}
    public int getPreTrainedSize(){return preTrained.size();}
    public Model getModel(){return model;}







}
