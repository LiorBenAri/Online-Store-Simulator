package bgu.spl.app;

/**
 * 
 * PurchaseSchedule is a class representing a single Purchase scheduling for a specific 
 * shoe type {@link shoeType} on a specific tick number {@link tick}.    
 */

public class PurchaseSchedule {
	private String shoeType;
	private int tick;
	
/**
 * 
 * given a shoe Type and a tick number, creates new Purchase Schedule.
 * 
 * @param shoeType - the shoe type that is scheduled to be Purchased.
 * @param tick - the tick number that specify when the Purchase is scheduled to occur. 
 */
	public PurchaseSchedule(String shoeType, int tick){
		this.shoeType=shoeType;
		this.tick=tick;
	}
	

	/**
	 * 
	 * @return the scheduled Purchase Shoe Type. 
	 */
	public String getPurchaseShoeType(){
		return shoeType;
	}
	
	/**
	 * 
	 * @return the tick number that specify when the Purchase is scheduled to occur. 
	 */
	public int getPurchaseTick(){
		return tick;
	}
}
