package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
     public enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Cluster cluster;
    private Model model; // current model gpu is working on
    private int capacity; // amount of batches could be stored at the same time
    private volatile int curCapacity; // amount of possible databatches to send/receive
    private volatile long time; // current tick
    private int tick; // num of ticks to train  process data
    private ConcurrentLinkedDeque<DataBatch> preTrained; // databatch that has been processed by a cpu
    private ConcurrentLinkedDeque<DataBatch> preProcessed; // databatch that is needed to be processed by a cpu
    private boolean isDone; // does the gpu finished training current model
    private int curIdx;
    //Nir's implement
    private long curTime;
    private DataBatch curTraining;
    private GPUService gpuService;


    public GPU (Type type){
        this.type = type;
        cluster = Cluster.getInstance();
        preTrained = new ConcurrentLinkedDeque<DataBatch>();
        preProcessed = new ConcurrentLinkedDeque<DataBatch>();
        time = 1;
        curTime = 1;
        model = null;
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
        isDone = true; // Nir's implement
        cluster.addGPU(this);
        //Nir's implement
        curIdx = 0 ;
        curTraining = null;
        gpuService = null;
    }
    /**
     * @pre @param model isn't null
     * @post this.model = model
     *
     */

    public void setModel(Model model){
        if(model == null )
            throw new IllegalArgumentException("model argument is null");
        this.model = model;
        curCapacity = capacity;
        isDone = false;
        curIdx = 0;
        splitData();
        sendToClusterFirst();
    }

    public void setGpuService(GPUService gpuService_){
        this.gpuService = gpuService_;
    }

    public long getTime() {
        return time;
    }

    /**
     * @post time is increased by 1
     */
    public void updateTime(){
        time = time + 1;
        if(!isDone) {
            sendToCluster();
            train();
        }
    }


    private void splitData() {
        Data data = model.getData();
        while (curIdx < data.getSize()) {
            if (curIdx < data.getSize()) {
                preProcessed.addLast(new DataBatch(data, curIdx));
                curIdx = curIdx + 1000;
            }
        }
    }

    /**
     *send data to be processed only if there is enough space to receive it back
     */

    private void sendToClusterFirst(){
        for(int i=0; i <= capacity/2 && !preProcessed.isEmpty(); i=i+1) {  // will send to cluster when the model first set
            curCapacity = curCapacity - 1;
            synchronized (preProcessed) {
                cluster.receiveToProcess(preProcessed.removeFirst(), this);
            }
        }
    }

    private void sendToCluster() {
        if(curCapacity > 0 && !preProcessed.isEmpty() ){
            curCapacity = curCapacity - 1;
            synchronized (preProcessed) {
                cluster.receiveToProcess(preProcessed.removeFirst(), this);
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
    public void insertProcessed(DataBatch processed)  {
        if(curCapacity < 0)
            throw new IllegalStateException(" cannot except new processed databatches");
        synchronized (preTrained){
            preTrained.addLast(processed);
        }
    }



    /**
     *  number of ticks required deteremined by GPU type
     *  when finished training all data, need to finish event
     */

    private void train() {
            if (curTraining == null) {
                if (!preTrained.isEmpty()) {
                    synchronized (preTrained) {
                        curTraining = preTrained.removeFirst();
                    }
                    curTime = time;
                }
            } else
                if (time - curTime >= tick) {
                    model.getData().updateProcess(1000);
                    cluster.addGPUTime(1); // STATISTICS
                    curCapacity = curCapacity + 1;
                    if (model.getData().getSize() <= model.getData().getProcessed()) {
                        isDone = true;
                        gpuService.gpuComplete();
                        cluster.addModelTrained(model.getName()); //STATISTICS
                    }
                else {
                    if (!preTrained.isEmpty()) {
                        synchronized (preTrained) {
                            curTraining = preTrained.removeFirst();
                        }
                        curTime = time;
                    } else
                        curTraining = null;
                }
            }
                else {
                    cluster.addGPUTime(1); // STATISTICS
                }
        }

    public boolean isDone(){ return isDone;}
    public int getCapacity(){return capacity;}
    public int getPreTrainedSize(){return preTrained.size();}
    public Model getModel(){return model;}







}
