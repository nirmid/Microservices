package bgu.spl.mics.application.objects;


import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private HashMap<DataBatch,GPU> dataBMap;
	private static boolean isDone = false;
	private static Cluster cluster = null;
	private LinkedList<CPU> CPUS; // collection of CPUS
	private LinkedList<GPU> GPUS; // collection of GPUS
	private LinkedList<DataBatch> toProcess;
	private PriorityQueue<CpuPair> cpuPairs; // no need
	// statistics
	private LinkedList<String> namesModelTrained;
	private AtomicInteger dataBatchProcess; // Nir's implement
	private AtomicInteger CPUTime; // Nir's implement
	private AtomicInteger GPUTime; // Nir's implement
	private Object dataBatchP = new Object();
	private Object CPUT = new Object();
	private Object GPUT = new Object();



	private Cluster(){
		CPUS = new LinkedList<CPU>();
		GPUS = new LinkedList<GPU>();
		cpuPairs = new PriorityQueue<CpuPair>(); // no need
		dataBMap = new HashMap<DataBatch,GPU>();
		toProcess = new LinkedList<DataBatch>();
		namesModelTrained = new LinkedList<String>();
		dataBatchProcess = new AtomicInteger(0); // Nir's implement
		CPUTime = new AtomicInteger(0); // Nir's implement
		GPUTime = new AtomicInteger(0); // Nir's implement
	}
	public void addCPU(CPU cpu){
		synchronized (CPUS) {
			CPUS.add(cpu);
		}
	}  // need to add cpu to CPUS and create CPUpair and add it priorityQueue

	public void addGPU(GPU gpu){
		synchronized (GPUS) {
			GPUS.add(gpu);
		}
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

	public DataBatch getDataBatch(){  // function used by a cpu to receive a databatch to process
		while(toProcess.isEmpty()) {
			try {
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException e) {}
		}
		DataBatch dataBatch;
		synchronized (toProcess){
				if(toProcess.isEmpty())
					getDataBatch();
				dataBatch =  toProcess.removeFirst();
		}
		return dataBatch;
	}

	public DataBatch getDataBatch2(){ // Nir's implement
		DataBatch dataBatch= null;
		synchronized (toProcess) { // toProcess should be BlockingQueue, is it thread safe? (removing)
			if(!toProcess.isEmpty())
				dataBatch = toProcess.removeFirst();
		}
		return dataBatch;
	}

	/*public DataBatch getDataBatach2(){  // function used by a cpu to receive a databatch to process
		while(toProcess.isEmpty())
			try{
				wait();
			}catch (InterruptedException e){}
		synchronized (toProcess){
			if(toProcess.isEmpty())
				getDataBatch();
			return toProcess.removeFirst();
		}
	}*/

	public void receiveToProcess(DataBatch dataBatch,GPU gpu){
		synchronized (dataBMap){
			dataBMap.put(dataBatch,gpu);
		}
		synchronized (toProcess){
			toProcess.add(dataBatch);
		}
		synchronized (this) {
			notifyAll();
		}

//		CPU cpu; // no need
//		synchronized (cpuPairs) {
//			CpuPair cpuPair = cpuPairs.poll();
//			cpu = cpuPair.getCpu();
//			cpuPair.setTicks(cpuPair.getTicks() + cpu.processTime(dataBatch.getType()));
//			cpuPairs.add(cpuPair);
//		}
//		cpu.recieveDataBatch(dataBatch);
	}


	/**
	 * function will receive processed databatch from cpu , and will update the relevant gpu
	 * @param dataBatch
	 */
	public void receiveToTrain(DataBatch dataBatch){
//		if ( dataBatch != null) {
			GPU gpu = dataBMap.get(dataBatch);
			synchronized (dataBMap) {
				dataBMap.remove(dataBatch);
			}
			gpu.insertProcessed(dataBatch);
//		}
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		if (isDone == false) {
			synchronized (Cluster.class) {
				if (isDone == false) {
					cluster = new Cluster();
					isDone = true;
				}
			}
		}
		return cluster;
	}


}
