package bgu.spl.app.data;
import bgu.spl.app.mics.WebsiteClientService;

/**
 * 
 * CustomerServiceInput is a class corresponding to the class {@link WebsiteClientService}, 
 * which responsible of handling json input of related to the WebsiteClientService class.
 *
 */
public class CustomerServiceInput {
	
	private String name;
	private String[] wishList;
	private PurchaseScheduleInput[] purchaseSchedule;
	
	/**
	 * {@link WebsiteClientService#WebsiteClientService(String, java.util.List, java.util.Set, java.util.concurrent.CountDownLatch, java.util.concurrent.CountDownLatch)}.
	 */
	public CustomerServiceInput(String _name, String[] _wishList, PurchaseScheduleInput[] _purchaseSchedule){
		name = _name;
		wishList = _wishList;
		purchaseSchedule = _purchaseSchedule;
	}
	
	/**
	 * 
	 * 	{@link WebsiteClientService#getName()}
	 */
	public String getName(){
		return name;
	}
	

	/**
	 * 
	 * 	@return WebsiteClientService wishlist.
	 */
	public String[] getWishList(){
		return wishList;
	}
	
	/**
	 * 
	 * @return WebsiteClientService PurchaseSchedule.
	 */
	public PurchaseScheduleInput[] getPurchaseSchedule(){
		return purchaseSchedule;
	}
	
	
	
	
}
