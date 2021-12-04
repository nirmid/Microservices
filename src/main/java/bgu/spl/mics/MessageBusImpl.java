package bgu.spl.mics;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBus bus;

	@Override
	/**
	 * @param
	 * @post MicroService m is subscribed to Event of type
	 */
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub

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
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * @param b 	The message to added to the queues.
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * @param e     	The event to add to the queue.
	 * @param <T>
	 * @return
	 */
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @param m the micro-service to create a queue for.
	 */
	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * @param m the micro-service to unregister.
	 */
	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return
	 * @throws InterruptedException
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @param m
	 * @param type
	 * @return true if m is registered to Broadcast type
	 */
	public boolean isRegisterBroadcast(MicroService m ,Class<? extends Broadcast> type ){return false;}

	/**
	 *
	 * @param m
	 * @param type
	 * @param <T>
	 * @return true if m is registered to Event type
	 */
	public <T> boolean isRegisterEvent(MicroService m ,Class<? extends Event<T>> type ){return false;}

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
