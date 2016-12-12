package bgu.spl.app.messages;
import bgu.spl.mics.Broadcast;

/**
 * 
 * NewDiscountBroadcast class represents a Broadcast of a new Discount, for a specific shoe type on specific amount.
 *
 */
public class NewDiscountBroadcast implements Broadcast{
	private int broadcastTick;
	private	String shoeType;

	/**
	 * given a shoe type and the tick when the broadcast Was Made, create new NewDiscountBroadcast
	 * According to this parameters.
	 * 
	 * @param _shoeType - the shoe type that the broadcast announces as discounted.
	 * @param _broadcastTick - the tick when the Discount Broadcast Was Made.
	 */
	public NewDiscountBroadcast(int _broadcastTick, String _shoeType){
		broadcastTick = _broadcastTick;		
		shoeType = _shoeType;
	}
	
	/**
	 * 
	 * @return the tick when the Discount Broadcast Was Made.
	 */
	public int getBroadcastTick(){
		return broadcastTick;
	}
	

	/**
	 * 
	 * @return the shoe type that the broadcast announces as discounted.
	 */
	public String getShoeTypeToDiscount(){
		return shoeType;
	}
	

}
