package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.example.messages.ExampleBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private TimerTask timer;
	private long currentTime;
	private int duration;
	private int speed;

	public TimeService(int _duration,int _speed) {
		super("TimerService");
		speed = _speed;
		duration = _duration;
		currentTime = 0 ;
		// TODO Implement this

	}
	@Override
	protected void initialize() {

		// TODO Implement this
	}

}
