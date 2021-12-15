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
    private boolean terminated;

    public CPU(int _cores){ //constractor
        data = new LinkedList<DataBatch>();
        cores = _cores;
        cluster = Cluster.getInstance();
        time = 1;
        terminated = false;

    }

    /**
     * process the data form linkedList and send the processed data to cluster
     * @pre data != null && data.getFirst() != null
     * @post @pre(data.getFirst()) != data.getFirst()
      */
    public void processData() throws InterruptedException {
        while (!terminated) {
            while (data.isEmpty())
                wait();
            long currentTime = time;
            DataBatch currentData;
            synchronized (data) {
                currentData = data.removeFirst();
            }
            long processTime = prcoessTime(currentData.getType());
            while (time - currentTime < processTime)
                wait();
            cluster.receiveToTrain(currentData);
        }
    }
    public void processData2(){ // need to be changes
            DataBatch currentData = cluster.getDataBatach();
            long currentTime = time;
            synchronized (data) {
                currentData = data.removeFirst();
            }
            long processTime = prcoessTime(currentData.getType());
            while (time - currentTime < processTime)
                try{
                    wait();
                }catch (InterruptedException e){}
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
        processData2(); // will only need to update
        notifyAll();
    } //  update time according to TimerService
    public void terminateCpu(){terminated = true;}
    public int getCores(){return cores;} // returns cores
    public LinkedList<DataBatch> getData(){return data;} // returns data
    public long getTime(){return time;} // returns time

    public long prcoessTime(Data.Type type){
        long processTime;
        switch (type) {
            case Images:
                processTime = ((long) (32 / cores)) * 4;
                break;
            case Text:
                processTime = ((long) (32 / cores)) * 2;
                break;
            case Tabular:
                processTime = ((long) (32 / cores));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return processTime;
    }


}
