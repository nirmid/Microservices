package bgu.spl.mics.application.objects;


import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

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
	private int dataBatchProcess;
	private int CPUTime;
	private int GPUTime;
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
		dataBatchProcess = 0;
		CPUTime = 0;
		GPUTime = 0;
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

	public void addDataBatchProcess(){ // STATISTICS
		synchronized (dataBatchP){
			dataBatchProcess = dataBatchProcess +1;
		}
	}

	public void addCPUTime(int time){ // STATISTICS
		synchronized (CPUT) {
			CPUTime = CPUTime + time;
		}
	}

	public void addGPUTime(int time){ // STATISTICS
		synchronized (GPUT) {
			GPUTime = GPUTime + time;
		}
	}

	public DataBatch getDataBatch(){  // function used by a cpu to receive a databatch to process
		while(toProcess.isEmpty())
			try{
				wait();
			}catch (InterruptedException e){}
		synchronized (toProcess){
				if(toProcess.isEmpty())
					getDataBatch();
				return toProcess.removeFirst();
		}
	}

	public DataBatch getDataBatach2(){  // function used by a cpu to receive a databatch to process
		while(toProcess.isEmpty())
			try{
				wait();
			}catch (InterruptedException e){}
		synchronized (toProcess){
			if(toProcess.isEmpty())
				getDataBatch();
			return toProcess.removeFirst();
		}
	}

	public void receiveToProcess(DataBatch dataBatch,GPU gpu){
		synchronized (dataBMap){
			dataBMap.put(dataBatch,gpu);
		}
		synchronized (toProcess){
			toProcess.add(dataBatch);
		}
		notifyAll();

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
