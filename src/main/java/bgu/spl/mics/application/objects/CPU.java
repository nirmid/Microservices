package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Cluster cluster; // should be instance
    private long time;
    private boolean terminated;
    private long currentTime;
    private  long processTime;
    private DataBatch currentData;

    public CPU(int _cores){ //constractor
        cores = _cores;
        cluster = Cluster.getInstance();
        time = 1;
        processTime = 0; // only needed for Nir's implement
        currentTime = 1;
        terminated = false;
        currentData = null;
        cluster.addCPU(this);

    }

    /**
     * process the data form linkedList and send the processed data to cluster
     * @pre data != null && data.getFirst() != null
     * @post @pre(data.getFirst()) != data.getFirst()
      */
    public void processData() {
        while (!terminated) {
            DataBatch currentData = cluster.getDataBatch();
            long currentTime = time;
            long processTime = processTime(currentData.getType());
            while (time - currentTime < processTime)
                try{
                    cluster.addCPUTime(1); // STATISTICS
                    synchronized (this) {
                        wait();
                    }
                }catch (InterruptedException e){
                    if (terminated)
                        processData();
                }
            cluster.addDataBatchProcess(); // STATISTICS
            cluster.receiveToTrain(currentData);
        }
        System.out.println("Thread CPU ProcessData is terminated" );
    }

   public void processData2() {
       if (currentData != null) {
           if (time - currentTime >= processTime) {
               cluster.receiveToTrain(currentData);
               currentData = cluster.getDataBatch2();
               cluster.addCPUTime(1);
           } else
               cluster.addCPUTime(1);
       } else {
           currentData = cluster.getDataBatch2();
       }
   }



    /**
     * update time
     * @post time = _time
     */
    public void updateTime(){
        time=time +1;
        synchronized (this) {
            notifyAll();
        }
    } //  update time according to TimerService

    public void updateTime2(){
        time = time+1;
        processData2();

    }
    public void terminateCpu(){
        terminated = true;
        synchronized (this) {
            notifyAll();
        }
    }
    public int getCores(){return cores;} // returns cores
    public long getTime(){return time;} // returns time

    public long processTime(Data.Type type){
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
