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

    public CPU(int _cores,LinkedList<DataBatch> _data,Cluster _cluster,int _time){} // constractor

    /**
     * @pre
     */
    public void processData(){} // process the data form linkedlist and send the processed data to cluster
    public void recieveDataBatch(){} //  add dataBatch to data
    public void updateTime(int _time){} //  update time according to TimerService
    public int getCores(){return 1;} // returns cores
    public LinkedList<DataBatch> getData(){return data;} // returns data
    public Cluster getCluster(){return cluster;}// returns cluster
    public long getTime(){return time;} // returns time


}
