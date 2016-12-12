package bgu.spl.app.messages;
import bgu.spl.mics.Broadcast;

/**
 * 
 * TickBroadcast class represents a single Tick Broadcast.
 * a Broadcast announce the current tick to all the requesters of this broadcast.
 *
 */
public class TickBroadcast implements Broadcast {
	private int currTick;
	
	/**
	 * creates new TickBroadcast.
	 * 
	 * @param _currTick - the current tick - number of ticks that passed since the program started working.
	 */
	public TickBroadcast(int _currTick){
		currTick = _currTick;
	}
	
	/**
	 * 
	 * @return the current tick - number of ticks that passed since the program started working.
	 */
	public int getCurrTick(){
		return currTick;
	}

}
