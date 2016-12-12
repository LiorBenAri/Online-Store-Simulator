package bgu.spl.app.data;

import bgu.spl.app.PurchaseSchedule;

/**
 * 
 * PurchaseScheduleInput is a class corresponding to the class {@link PurchaseScheduleInput}, 
 * which responsible of handling json input of type PurchaseScheduleInput.
 *
 */
public class PurchaseScheduleInput {
	private String shoeType;
	private int tick;
	
	/**
	 *{@link PurchaseSchedule#PurchaseSchedule(String, int)}.
	 */
	public PurchaseScheduleInput(String shoeType, int tick){
		this.shoeType=shoeType;
		this.tick=tick;
	}
	
	/**
	 *{@link PurchaseSchedule#getPurchaseShoeType()}.
	 */
	public String getPurchaseShoeType(){
		return shoeType;
	}

	/**
	 *{@link PurchaseSchedule#getPurchaseTick()}.
	 */
	public int getPurchaseTick(){
		return tick;
	}
}
