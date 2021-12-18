package bgu.spl.mics.application.objects;



/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Cluster cluster;
    private long time;
    private long currentTime;
    private  long processTime;
    private DataBatch currentData;

    public CPU(int _cores){
        cores = _cores;
        cluster = Cluster.getInstance();
        time = 1;
        processTime = 0;
        currentTime = 1;
        currentData = null;
        cluster.addCPU(this);

    }

    /**
     * process the data form cluster and send the processed data to cluster
     * @pre cluster isn't null
     * @post if cluster is null, try get databatch from cluster. else,if databatch is processed, send back to cluster and try get another databatch
      */

   private void processData() {
       if (currentData != null) {
           if (time - currentTime >= processTime) {
               cluster.addDataBatchProcess(); // STATISTICS
               cluster.receiveToTrain(currentData);
               cluster.addCPUTime(1);
               currentData = cluster.getDataBatch();
               if(currentData != null) {
                   currentTime = time;
                   processTime = processTime(currentData.getType());
               }
           }
           else {
               cluster.addCPUTime(1);
           }
       } else {
           currentData = cluster.getDataBatch();
       }
   }


    /**
     * update time
     * @post time = _time
     */

    public void updateTime(){
        time = time+1;
        processData();

    }

    public int getCores(){return cores;} // returns cores

    public long getTime(){return time;} // returns time

    private long processTime(Data.Type type){
        long processTime;
        switch (type) {
            case Images:
                processTime = ((long) (32 / cores)) * 4;
                break;
            case Text:
                processTime = ((long) (32 / cores)) * 2;
                break;
            case Tabular:
                processTime = (long) (32 / cores);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return processTime;
    }


}
