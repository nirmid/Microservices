package bgu.spl.mics;

import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.services.GPUService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBusImpl bus = null; // added nothing
	private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventMap; // holds microservice linkedlist which are subscribed to some event type
	private ConcurrentHashMap<Class<? extends Broadcast>,ConcurrentLinkedQueue<MicroService>> broadcastMap; // holds microservice linkedlist which are subscribed to some broadcast type
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> microMap; // holds messages queues for each microservice
	private ConcurrentHashMap<Event,Future> futureMap; // holds future that is associated with an event
	private static boolean isDone = false;
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Message>>> registers;  // list of lists that a microservice is registered to


	private MessageBusImpl(){
		eventMap = new ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>>();
		broadcastMap = new ConcurrentHashMap<Class<? extends Broadcast>,ConcurrentLinkedQueue<MicroService>>();
		microMap = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>>();
		futureMap = new ConcurrentHashMap<Event,Future>();
		registers = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Message>>>();
	}
	@Override
	/**
	 * @param
	 * @post MicroService m is subscribed to Event of type
	 */
	public  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		ConcurrentLinkedQueue<MicroService> list = new ConcurrentLinkedQueue<>();
		list.add(m);
		if(eventMap.putIfAbsent(type, list) != null)
			eventMap.get(type).add(m);
		ConcurrentLinkedQueue<Class<? extends Message>> mList = new ConcurrentLinkedQueue<Class<? extends Message>>();
		mList.add(type);
		if (registers.putIfAbsent(m, mList) != null)
			registers.get(m).add(type);
	}

	/**
	 *
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing micro-service.
	 * @post MicroService m is subscribed to Broadcast of type
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		ConcurrentLinkedQueue<MicroService> list = new ConcurrentLinkedQueue<>();
		list.add(m);
		if(broadcastMap.putIfAbsent(type, list) != null)
			broadcastMap.get(type).add(m);
		ConcurrentLinkedQueue<Class<? extends Message>> mList = new ConcurrentLinkedQueue<Class<? extends Message>>();
		mList.add(type);
		if (registers.putIfAbsent(m, mList) != null)
			registers.get(m).add(type);
	}

	/**
	 *
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 * @param <T>
	 * @pre  microservice has registered and subscribed to Event e && event has been sent
	 * @post processing of event has finished with result param
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		futureMap.get(e).resolve(result);
	}

	/**
	 *
	 * @param b 	The message to added to the queues.
	 * @pre  there is a microservice that has been subscribed to the broadcast
	 * @post Broadcast b has been sent to the relevant microservice
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (broadcastMap.get(b.getClass())) {
			ConcurrentLinkedQueue<MicroService> subscribers = broadcastMap.get(b.getClass());
			for (MicroService m : subscribers) {
				ConcurrentLinkedQueue<Message> list = microMap.get(m);
				if (list != null) {
					(list).add(b);
				}
			}
		}
		synchronized (this) {
			notifyAll();
		}
	}

	/**
	 *
	 * @param e     	The event to add to the queue.
	 * @param <T>
	 * @return
	 * @pre there is a microservice that has been subscribed to the event
	 * @post Event e has been sent to the relevant microservice
	 */
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if(eventMap.get(e.getClass()) == null)
			return null;
		Future<T> future = new Future<T>();
		synchronized (futureMap) {
			futureMap.put(e, future);
		}
		ConcurrentLinkedQueue<MicroService> list = eventMap.get(e.getClass());
		MicroService m;
		synchronized (list) {
			m = list.poll();
			list.add(m);
		}
		microMap.get(m).add(e);
		synchronized (this) {
			notifyAll();
		}
		return future;
	}

	/**
	 * @pre MicroService m is not currently registered
	 * @post MicroService m is registered and has a queue at MessageBus
	 * @param m the micro-service to create a queue for.
	 */
	@Override
	public void register(MicroService m) {
		ConcurrentLinkedQueue<Message> list = new ConcurrentLinkedQueue<Message>();
		microMap.put(m, list);
	}

	/**
	 * @pre a MicroService m is registered
	 * @post a MicroService m is not registered
	 * @param m the micro-service to unregister.
	 */
	@Override
	public void unregister(MicroService m) {
		ConcurrentLinkedQueue<Class<? extends Message>> mList = registers.get(m);
		for (Class<? extends Message> t : mList) {
			if(eventMap.get(t) != null)
				eventMap.get(t).remove(m);
			else if(broadcastMap.get(t) != null)
				broadcastMap.get(t).remove(m);
		}
		microMap.remove(m);
		registers.remove(m);
	}

	/**
	 *
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return
	 * @throws InterruptedException
     * @pre
     *
	 */

	@Override
	public Message awaitMessage(MicroService m)  { //Nir's implement
		while (microMap.get(m).isEmpty()) {
			try {
				synchronized (this) {
					wait();
				}
			}catch (InterruptedException e){}
		}
		return microMap.get(m).poll();
	}

	/**
	 *
	 * @return
	 */
	public static MessageBusImpl getInstace() {
		if (isDone == false) {
			synchronized (MessageBusImpl.class) {
				if (isDone == false) {
					bus = new MessageBusImpl();
					isDone = true;
				}
			}
		}
		return bus;
	}


	

}
