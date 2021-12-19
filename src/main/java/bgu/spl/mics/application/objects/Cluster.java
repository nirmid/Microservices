package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private ConcurrentHashMap<DataBatch,GPU> dataBMap;
	private static boolean isDone = false;
	private static Cluster cluster = null;
	private ConcurrentLinkedQueue<CPU> CPUS; // collection of CPUS
	private ConcurrentLinkedQueue<GPU> GPUS; // collection of GPUS
	private ConcurrentLinkedQueue<DataBatch> toProcess;
	// statistics
	private LinkedList<String> namesModelTrained;
	private AtomicInteger dataBatchProcess;
	private AtomicInteger CPUTime;
	private AtomicInteger GPUTime;
	private Object dataBatchP = new Object();
	private Object CPUT = new Object();
	private Object GPUT = new Object();



	private Cluster(){
		CPUS = new ConcurrentLinkedQueue<CPU>();
		GPUS = new ConcurrentLinkedQueue<GPU>();
		dataBMap = new ConcurrentHashMap<DataBatch,GPU>();
		toProcess = new ConcurrentLinkedQueue<DataBatch>();
		namesModelTrained = new LinkedList<String>();
		dataBatchProcess = new AtomicInteger(0);
		CPUTime = new AtomicInteger(0);
		GPUTime = new AtomicInteger(0);
	}
	public void addCPU(CPU cpu){
		CPUS.add(cpu);
	}  // need to add cpu to CPUS and create CPUpair and add it priorityQueue

	public void addGPU(GPU gpu){
		GPUS.add(gpu);
	}

	public void addModelTrained(String name){ // STATISTICS
		synchronized (namesModelTrained){
			namesModelTrained.addLast(name);
		}
	}

	public AtomicInteger getCPUTime() {
		return CPUTime;
	}

	public AtomicInteger getGPUTime() {
		return GPUTime;
	}

	public AtomicInteger getDataBatchProcess() {
		return dataBatchProcess;
	}

	public LinkedList<String> getNamesModelTrained() {
		return namesModelTrained;
	}

	public void addDataBatchProcess(){ // STATISTICS
		synchronized (dataBatchP){
			dataBatchProcess.getAndIncrement();
		}
	}

	public void addCPUTime(int time){ // STATISTICS
		synchronized (CPUT) {
			CPUTime.getAndIncrement();
		}
	}

	public void addGPUTime(int time){ // STATISTICS
		synchronized (GPUT) {
			GPUTime.getAndIncrement();
		}
	}

	public DataBatch getDataBatch(){
		DataBatch dataBatch= null;
		if(!toProcess.isEmpty())
			dataBatch = toProcess.poll();
		return dataBatch;
	}

	public void receiveToProcess(DataBatch dataBatch,GPU gpu){
		dataBMap.put(dataBatch,gpu);
		toProcess.add(dataBatch);
		synchronized (this) {
			notifyAll();
		}
	}


	/**
	 * function will receive processed databatch from cpu , and will update the relevant gpu
	 * @param dataBatch
	 */
	public void receiveToTrain(DataBatch dataBatch){
		if ( dataBatch != null) {
			GPU gpu = dataBMap.get(dataBatch);
			dataBMap.remove(dataBatch);
			gpu.insertProcessed(dataBatch);
		}
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		if (!isDone) {
			synchronized (Cluster.class) {
				if (!isDone) {
					cluster = new Cluster();
					isDone = true;
				}
			}
		}
		return cluster;
	}


}
