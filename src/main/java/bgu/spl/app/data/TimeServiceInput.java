package bgu.spl.app.data;

import bgu.spl.app.mics.TimeService;

/**
 * 
 * TimeServiceInput is a class corresponding to the class {@link TimeService}, 
 * which responsible of handling json input related to the TimeService class.
 *
 */
public class TimeServiceInput {
	
	private int speed;
	private int duration;

	/**
	 * {@link TimeService#TimeService(int, int, java.util.concurrent.CountDownLatch, java.util.concurrent.CountDownLatch)}.
	 */
	public TimeServiceInput(int _speed, int _duration){
		speed = _speed;
		duration = 	_duration;	
	}
	
	/**
	 * 
	 * @return the number of milliseconds each tick takes.
	 */
	public int getSpeed(){
		return speed;
	}
	
	/**
	 * 
	 * @return the number of ticks the program runs.
	 */
	public int getDuration(){
		return duration;
	}

}
