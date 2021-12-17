package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.GPUService;

import java.util.LinkedList;
import java.util.Set;
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
    private long time; // current tick
    private int tick; // num of ticks to train  process data
    private LinkedBlockingDeque<DataBatch> preTrained; // databatch that has been processed by a cpu
    private LinkedBlockingDeque<DataBatch> preProcessed; // databatch that is needed to be processed by a cpu
    private boolean isDone; // does the gpu finished training current model
    private Object lock1 = new Object();
    private int curIdx;
    private boolean terminated;
    //Nir's implement
    private long curTime;
    private DataBatch curTraining;
    private GPUService gpuService;


    public GPU (Type type){
        this.type = type;
        cluster = Cluster.getInstance();
        preTrained = new LinkedBlockingDeque<DataBatch>();
        preProcessed = new LinkedBlockingDeque<DataBatch>();
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
        isDone = false;
        cluster.addGPU(this);
        terminated = false;
        //Nir's implement
        curIdx = 0 ;
        curTraining = null;
        gpuService = null;
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
        isDone = false;
        curIdx = 0;
        Thread split = new Thread(()->
                splitData());
        split.start();
        Thread send = new Thread(()->
                sendToCluster());
        send.start();
        Thread train = new Thread(()->
                train());
        train.start();
        split.join();
        send.join();
        train.join();
    }

    public void setGpuService(GPUService gpuService_){ // Nir's implement
        this.gpuService = gpuService_;
    }

    public void setModel2(Model model){ // Nir's implement
        this.model = model;
        curCapacity = capacity;
        isDone = false;
        curIdx = 0;
        splitData2();
        sendToClusterFirst();
    }

    public void updateTime(){
        time = time + 1;
        synchronized (this) {
            notifyAll();
        }
    }


    public void updateTime2(){  // Nir's implement
        time = time + 1;
        sendToCluster2();
        train2();
    }

    private void splitData(){
        Data data = model.getData();
        while(curIdx < data.getSize()){
            preProcessed.addLast(new DataBatch(data,curIdx));
            synchronized (this) {
                notifyAll();
            }
            curIdx = curIdx + 1000;
        }
        System.out.println("Thread splitData is terminated" );
    }

    private void splitData2() {  // Nir's implement
        Data data = model.getData();
        while (curIdx < data.getSize()) {
            if (curIdx < data.getSize()) {
                preProcessed.addLast(new DataBatch(data, curIdx));
                curIdx = curIdx + 1000;
            }
        }
        System.out.println("splitdata process finished");
    }

    /**
     *send data to be processed only if there is enough space to receive it back
     */
    private void sendToCluster() {
        while(!terminated) {
            while (curIdx < model.getData().getSize() || !preProcessed.isEmpty()) {
                while (curCapacity < 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        if (terminated)
                            sendToCluster();
                    }
                }
                synchronized (lock1) {
                    if (curCapacity > 0 && !preProcessed.isEmpty()) {
                        curCapacity = curCapacity - 1;
                        synchronized (preProcessed) {
                            cluster.receiveToProcess(preProcessed.removeFirst(), this);
                        }
                    }
                }
            }
        }
        System.out.println("Thread sendToCluster is terminated" );
    }

    private void sendToClusterFirst(){  // Nir's implement
        for(int i=0; i <= capacity/3 && !preProcessed.isEmpty(); i=i+1) {  // will send to cluster when the model first set
            curCapacity = curCapacity - 1;
            synchronized (preProcessed) {
                cluster.receiveToProcess(preProcessed.removeFirst(), this);
            }
        }
        System.out.println("first send to cluster finished");
    }

    private void sendToCluster2() {  // Nir's implement
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
        while(!isDone & !terminated) {
            while (preTrained.isEmpty()) {
                try {
                    synchronized (this){
                        wait();
                    }
                }
                catch (InterruptedException e) {
                    if (terminated)
                        train();
                }
            }
            if(!preTrained.isEmpty())
                preTrained.removeFirst();
            else
                train();
            long currentTime = time;
            while (time - currentTime < tick) {
                try {
                    cluster.addGPUTime(1); // STATISTICS
                    synchronized (this) {
                        wait();
                    }
                }
                catch (InterruptedException e) {
                    if (terminated)
                        train();
                }
            }
            model.getData().updateProcess(1000);
            synchronized (lock1) {
                curCapacity = curCapacity + 1;
            }
            if (model.getData().getSize() <= model.getData().getProcessed()) {
                isDone = true;
                cluster.addModelTrained(model.getName()); //STATISTICS
            }
        }
        System.out.println("Thread train is terminated" );
    }

    private void train2() {  // Nir's implement
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
            else{
                if (!preTrained.isEmpty()) {
                    synchronized (preTrained) {
                        curTraining = preTrained.removeFirst();
                    }
                    curTime = time;
                }
                else
                    curTraining = null;
            }
        }
            else
                cluster.addGPUTime(1); // STATISTICS
    }

    public void terminate(){
        terminated = true;
        synchronized (this) {
            notifyAll();
        }
    }
    public boolean isDone(){ return isDone;}
    public int getCapacity(){return capacity;}
    public int getPreTrainedSize(){return preTrained.size();}
    public Model getModel(){return model;}







}
