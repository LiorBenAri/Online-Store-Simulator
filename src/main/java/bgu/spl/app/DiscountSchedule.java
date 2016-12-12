package bgu.spl.app;

/**
 * 
 * DiscountSchedule is a class representing a single Discount scheduling for a specific amount {@link amount} of specific 
 * shoe type {@link shoeType} on a specific tick number {@link tick}.    
 */

public class DiscountSchedule {
	private String shoeType;
	private int tick;
	private int amount;
	
/**
 * 
 * given a shoe Type, amount, and a tick number, creates new DiscountSchedule.
 * 
 * @param shoeType - the shoe type that is scheduled to be discounted.
 * @param tick - the tick number that specify when the discount is scheduled to occur.
 * @param amount - the amount of {@link shoeType} shoes that are scheduled to be discounted. 
 */
	public DiscountSchedule(String shoeType, int tick, int amount){
		this.shoeType=shoeType;
		this.tick=tick;
		this.amount=amount;
	}
	
	/**
	 * 
	 * @return the Discounted Shoe Type. 
	 */
	public String getDiscountShoeType(){
		return shoeType;
	}
	
	/**
	 * 
	 * @return the tick number that specify when the discount is scheduled to occur.
	 */
	public int getDiscountTick(){
		return tick;
	}
	
	/**
	 * 
	 * @return the amount of {@link shoeType} shoes that are discounted on this current discount.
	 */
	public int getDiscountedAmount(){
		return amount;
	}
}
