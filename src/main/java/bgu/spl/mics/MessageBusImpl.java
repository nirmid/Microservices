package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBus bus;
	private HashMap<Class<? extends Event>, LinkedList<MicroService>> eventMap; // holds microservice linkedlist which are subscribed to some event type
	private HashMap<Class<? extends Broadcast>,LinkedList<MicroService>> broadcastMap; // holds microservice linkedlist which are subscribed to some broadcast type
	private HashMap<MicroService, LinkedList<Message>> microMap; // holds messages queues for each microservice
	private HashMap<Event,Future> futureMap; // holds future that is associated with an event


	@Override
	/**
	 * @param
	 * @post MicroService m is subscribed to Event of type
	 */
	public  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(microMap.get(m) == null)
			throw new IllegalArgumentException("Microservice m has not registered");
		if(eventMap.get(type) !=null){
			LinkedList<MicroService> list= eventMap.get(type);
			synchronized(list) {
				list.addLast(m);
			}
		}
		else{
			synchronized(eventMap) {
				if(eventMap.get(type) !=null) {
					LinkedList<MicroService> list = eventMap.get(type);
					synchronized (list) {
						list.addLast(m);
					}
				}
				else{
						LinkedList<MicroService> list = new LinkedList<MicroService>();
						list.addLast(m);
						eventMap.put(type, list);
					}
			}

		}
	}

	/**
	 *
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing micro-service.
	 * @post MicroService m is subscribed to Broadcast of type
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (microMap.get(m) == null)
			throw new IllegalArgumentException("Microservice m has not registered");
		if (broadcastMap.get(type) != null) {
			LinkedList<MicroService> list = broadcastMap.get(type);
			synchronized (list) {
				list.addLast(m);
			}
		} else {
			synchronized (broadcastMap) {
				if (broadcastMap.get(type) != null) {
					LinkedList<MicroService> list = broadcastMap.get(type);
					synchronized (list) {
						list.addLast(m);
					}
				} else {
					LinkedList<MicroService> list = new LinkedList<MicroService>();
					list.addLast(m);
					broadcastMap.put(type, list);
				}
			}

		}
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
		if(futureMap.get(e) == null)
			throw new IllegalArgumentException("event e does not have associated future");
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
        LinkedList<MicroService> subscribers = broadcastMap.get(b.getClass());
        for (MicroService m : subscribers)
           synchronized (microMap.get(m)){
			(microMap.get(m)).addLast(b);
		   }
		notifyAll();
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
		LinkedList<MicroService> list = eventMap.get(e.getClass());
		synchronized (list) {
			MicroService m = list.removeFirst();
			list.addLast(m);
			synchronized (microMap.get(m)) {
				microMap.get(m).addLast(e);
			}
		}
		notifyAll();
		return future;
	}

	/**
	 * @pre MicroService m is not currently registered
	 * @post MicroService m is registered and has a queue at MessageBus
	 * @param m the micro-service to create a queue for.
	 */
	@Override
	public void register(MicroService m) {
			if (microMap.get(m) != null)
				throw new IllegalArgumentException("m already has registered");
			LinkedList<Message> list = new LinkedList<Message>();
			synchronized (microMap) {
				microMap.put(m, list);
			}
	}

	/**
	 * @pre a MicroService m is registered
	 * @post a MicroService m is not registered
	 * @param m the micro-service to unregister.
	 */
	@Override
	public void unregister(MicroService m) {
		for (Map.Entry<Class<? extends Event>, LinkedList<MicroService>> iter : eventMap.entrySet() )
			synchronized (iter.getValue()) {
				(iter.getValue()).remove(m);
			}
        for (Map.Entry<Class<? extends Broadcast>, LinkedList<MicroService>> iter : broadcastMap.entrySet() )
			synchronized (iter.getValue()) {
				(iter.getValue()).remove(m);
			}
		synchronized (microMap) {
			microMap.remove(m);
		}
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
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (microMap.get(m)) {
			while (microMap.get(m).isEmpty()) {
				wait();
			}
			return microMap.get(m).removeFirst();
		}
	}

	/**
	 *
	 * @return
	 */
	public static MessageBus getInstace(){
		if(bus == null)
			bus = new MessageBusImpl();
		return bus;
	}



	

}
