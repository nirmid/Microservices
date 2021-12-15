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

	// statistics ?


	private Cluster(){
		CPUS = new LinkedList<CPU>();
		GPUS = new LinkedList<GPU>();
		cpuPairs = new PriorityQueue<CpuPair>(); // no need
		dataBMap = new HashMap<DataBatch,GPU>();
		toProcess = new LinkedList<DataBatch>();
	}
	public void addCPU(CPU cpu){}  // need to add cpu to CPUS and create CPUpair and add it priorityQueue

	public void addGPU(GPU gpu){}

	public DataBatch getDataBatach(){  // function used by a cpu to receive a databatch to process
		while(toProcess.isEmpty())
			try{
				wait();
			}catch (InterruptedException e){}
		synchronized (toProcess){
				if(toProcess.isEmpty())
					getDataBatach();
				return toProcess.removeFirst();
		}
	}

	public void receiveToProcess(DataBatch dataBatch,GPU gpu){
		synchronized (dataBMap){
			dataBMap.put(dataBatch,gpu);
		}
		synchronized (toProcess){
			toProcess.add(dataBatch);
			notifyAll();
		}
		CPU cpu; // no need
		synchronized (cpuPairs) {
			CpuPair cpuPair = cpuPairs.poll();
			cpu = cpuPair.getCpu();
			cpuPair.setTicks(cpuPair.getTicks() + cpu.prcoessTime(dataBatch.getType()));
			cpuPairs.add(cpuPair);
		}
		cpu.recieveDataBatch(dataBatch);
	}


	/**
	 * function will receive processed databatch from cpu , and will update the relevant gpu
	 * @param dataBatch
	 */
	public void receiveToTrain(DataBatch dataBatch){
		GPU gpu = dataBMap.get(dataBatch);
		gpu.insertProcessed(dataBatch);
		synchronized (dataBMap){
			dataBMap.remove(dataBatch);
		}

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
