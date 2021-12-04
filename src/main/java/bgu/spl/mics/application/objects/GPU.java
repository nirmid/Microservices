package bgu.spl.mics.application.objects;

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
    private Model model;
    private int capacity;
    private int batchIdx ;
    private long timer;
    private Set<DataBatch> preTrained;


    public GPU (Type type, Cluster cluster){
    }

    /**
     * @pre
     *
     *
     *
     */
    private void sendToCluster(){}


    /**
     * @pre preTrained.size < capacity && processed.data == this.model.data
     * @inv preTrained.size <= capacity
     * @post preTrained.size <= capacity && @pre(preTrained.size) + 1 == preTrained.size && preTrained.getLast == processed
     *
     * @param processed
     */
    public void insertProcessed(DataBatch processed){}


    private void splitData(){}

    private  void train(){}

    /**
     * @pre preTrained.isEmpty()
     * @post this.model = model
     *
     *
     * @param model
     */
    public void setModel(Model model ){
    }





}
