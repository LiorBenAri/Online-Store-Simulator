package bgu.spl.app.data;

import bgu.spl.app.DiscountSchedule;

/**
 * 
 * DiscountScheduleInput is a class corresponding to the class {@link DiscountSchedule}, 
 * which responsible of handling json input related to the DiscountSchedule class.
 *
 */
public class DiscountScheduleInput {
	private String shoeType;
	private int tick;
	private int amount;
	
	/**
	 * {@link DiscountSchedule#DiscountSchedule(String, int, int)}.
	 */
	public DiscountScheduleInput(String shoeType, int tick, int amount){
		this.shoeType=shoeType;
		this.tick=tick;
		this.amount=amount;
	}
	

	/**
	 * {@link DiscountSchedule#getDiscountShoeType()}.
	 */
	public String getDiscountShoeType(){
		return shoeType;
	}
	
	/**
	 * {@link DiscountSchedule#getDiscountTick()}.
	 */
	public int getDiscountTick(){
		return tick;
	}
	
	/**
	 * {@link DiscountSchedule#getDiscountedAmount()}.
	 */
	public int getDiscountedAmount(){
		return amount;
	}
}
