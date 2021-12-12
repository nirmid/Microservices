package bgu.spl.mics;

import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl<linkedList> implements MessageBus {

	private static MessageBus bus;
	private HashMap<Class<? extends Event>, LinkedList<MicroService>> eventMap;
	private HashMap<Class<? extends Broadcast>,LinkedList<MicroService>> broadcastMap;
	private HashMap<MicroService,LinkedList<Message>> microMap;


	@Override
	/**
	 * @param
	 * @post MicroService m is subscribed to Event of type
	 */
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {


	}

	/**
	 *
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing micro-service.
	 * @post MicroService m is subscribed to Broadcast of type
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
            (microMap.get(m)).addLast(b);
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
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @pre MicroService m is not currently registered
	 * @post MicroService m is registered and has a queue at MessageBus
	 * @param m the micro-service to create a queue for.
	 */
	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

	}

	/**
	 * @pre a MicroService m is registered
	 * @post a MicroService m is not registered
	 * @param m the micro-service to unregister.
	 */
	@Override
	public void unregister(MicroService m) {
		for (Map.Entry<Class<? extends Event>, LinkedList<MicroService>> iter : eventMap.entrySet() )
		    (iter.getValue()).remove(m);
        for (Map.Entry<Class<? extends Broadcast>, LinkedList<MicroService>> iter : broadcastMap.entrySet() )
            (iter.getValue()).remove(m);
        microMap.remove(m);
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
		// TODO Auto-generated method stub
		return null;
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
