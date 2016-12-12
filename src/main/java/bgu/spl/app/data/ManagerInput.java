package bgu.spl.app.data;

import bgu.spl.app.mics.ManagementService;

/**
 * 
 * ManagerInput is a class corresponding to the class {@link ManagementService}, 
 * which responsible of handling json input related to the ManagementService class.
 *
 */
public class ManagerInput {
	
	DiscountScheduleInput[] discountSchedule;
	
	/**
	 * {@link ManagementService#ManagementService(java.util.List, java.util.concurrent.CountDownLatch, java.util.concurrent.CountDownLatch)}.
	 */
	public ManagerInput(DiscountScheduleInput[]  _discountSchedule){
	
		discountSchedule = _discountSchedule;
		
	}
	
	/**
	 * 
	 * @return the ManagementService DiscountSchedule.
	 */
	public DiscountScheduleInput[] getDiscountSchedule(){
		return discountSchedule;
	}

}
