package bgu.spl.mics.application.objects;


import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private HashMap<DataBatch,GPU> dataBMap;


	/**
	 * function will receive processed databatch from cpu , and will update the relevant gpu
	 * @param dataBatch
	 */
	public void receiveProcessed(DataBatch dataBatch){

	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return null;
	}


}
