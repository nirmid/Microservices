package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private LinkedList<DataBatch> data;
    private Cluster cluster; // should be instance
    private long time;

    public CPU(int _cores){ //constractor
        data = new LinkedList<DataBatch>();
        cores = _cores;
        cluster = Cluster.getInstance();
        time = 1;

    }

    /**
     * process the data form linkedList and send the processed data to cluster
     * @pre data != null && data.getFirst() != null
     * @post @pre(data.getFirst()) != data.getFirst()
      */
    public void processData() throws InterruptedException {
        while(data.isEmpty())
            wait();
        long currentTime = time;
        long processTime;
        DataBatch currentData;
        synchronized (data){
            currentData = data.removeFirst();
        }
        switch (currentData.getType()){
            case Images:
                processTime = ((long)(32/cores))* 4;
                break;
            case Text:
                processTime = ((long)(32/cores))*2;
                break;
            case Tabular:
                processTime = ((long)(32/cores));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentData.getType());
        }
        while(time-currentTime > processTime)
            wait();
        cluster.receiveProcessed(currentData);
    }

    /**
     * add dataBatch sent from cluster to data
     * @pre _data != null
     * @post data.getLast() == _data;
     */
    public void recieveDataBatch(DataBatch _data){
        synchronized (data){
            data.addLast(_data);
        }
        notifyAll();
    }

    /**
     * update time
     * @post time = _time
     */
    public void updateTime(){
        time=time +1;
        notifyAll();
    } //  update time according to TimerService
    public int getCores(){return cores;} // returns cores
    public LinkedList<DataBatch> getData(){return data;} // returns data
    public long getTime(){return time;} // returns time


}
