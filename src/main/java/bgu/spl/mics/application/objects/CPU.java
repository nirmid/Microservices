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
    private Cluster cluster;
    private long time;

    public CPU(int _cores,LinkedList<DataBatch> _data,Cluster _cluster,int _time){} //constractor

    /**
     * process the data form linkedList and send the processed data to cluster
     * @pre data != null && data.getFirst() != null
     * @post @pre(data.getFirst()) != data.getFirst()
      */
    public void processData(){}

    /**
     * add dataBatch sent from cluster to data
     * @pre _data != null
     * @post data.getLast() == _data;
     */
    public void recieveDataBatch(DataBatch _data){}

    /**
     * @param _time time as received from TimerService
     * @post time = _time
     */
    public void updateTime(int _time){} //  update time according to TimerService
    public int getCores(){return 1;} // returns cores
    public LinkedList<DataBatch> getData(){return data;} // returns data
    public Cluster getCluster(){return cluster;}// returns cluster
    public long getTime(){return time;} // returns time


}
