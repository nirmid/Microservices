package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Cluster;

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
	private TimerTask timerTask;
	private Timer timer;
	private long currentTime;
	private int duration;
	private int speed;
	private boolean isDone;

	public TimeService(int _duration,int _speed) {
		super("TimerService");
		speed = _speed;
		duration = _duration;
		currentTime = 1;
		timer = new Timer();
		isDone = false;
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (!isDone) {
					sendBroadcast(new TickBroadcast());
					currentTime = currentTime + 1;
					if (currentTime == duration)
						isDone = true;
				}
				else {
					sendBroadcast(new TerminateBroadcast());
					timer.cancel();
					Cluster cluster = Cluster.getInstance();
				}
			}
		};
	}
	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class,(t)-> {
			terminate();
		});
		timer.scheduleAtFixedRate(timerTask,speed,speed);
	}



}

