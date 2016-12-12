package bgu.spl.app.mics;
import bgu.spl.app.messages.TerminateBroadcast;
import bgu.spl.app.messages.TickBroadcast;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import bgu.spl.mics.MicroService;

/**
 * 
 * TimeService class is the timer service of the program.
 * this class is responsible to update all the services on every tick that passed.
 *
 */
public class TimeService extends MicroService {
	private  AtomicInteger currTick;
	private AtomicInteger speed;
	private int duration;
	private final Timer timer;
	protected static final Logger logger = Logger.getGlobal();

	/**
	 * creates new TimeService.
	 * 
	 * @param _speed - number of milliseconds each tick takes.
	 * @param _duration - number of ticks till the end of the program run.
	 * @param latch - {@link bgu.spl.mics.MicroService}
	 * @param finalLatch - {@link bgu.spl.mics.MicroService}
	 */	
	public TimeService(int _speed, int _duration, CountDownLatch latch,  CountDownLatch finalLatch){
		super("timer", latch, finalLatch);
		currTick = new AtomicInteger(0);
		this.speed = new AtomicInteger(_speed);
		duration=_duration;
		timer = new Timer();
	}

	public CountDownLatch getLatch(){
		return latch;
	}
	
	/**
	 * {@link bgu.spl.mics.MicroService}
	 * this method is starting the timer of the program.
	 */
	protected void initialize() {

		subscribeBroadcast(TerminateBroadcast.class, b -> {terminate();});
		subscribeBroadcast(TickBroadcast.class, b -> {this.currTick.set(b.getCurrTick());});

		//this method will count the ticks of the program and send TickTickBroadcasts to all services every tick that pass.
		timer.scheduleAtFixedRate(new TimerTask(){			
			public boolean helper = true;
			public CountDownLatch timer_latch = getLatch();	
			protected final Logger logger = Logger.getGlobal();

			public void run() {

				if (currTick.get()<=duration){


					try {
						if (helper){	
							logger.log(Level.INFO, "timer waiting for other threads....");
							timer_latch.await();//waiting till all services start running.
							logger.log(Level.INFO, "---timer is working---");
							helper = false;
						}

					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					currTick.incrementAndGet();
					TickBroadcast tickBroadcast = new TickBroadcast(currTick.get());
					sendBroadcast(tickBroadcast);



				}
				else{
					TerminateBroadcast terminateBroadcast = new TerminateBroadcast();
					sendBroadcast(terminateBroadcast);							

					timer.cancel();
					timer.purge();
					terminate();
				}								
			}	
		}, speed.get(),speed.get());
	}

}

