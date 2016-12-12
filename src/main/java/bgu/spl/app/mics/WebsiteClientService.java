package bgu.spl.app.mics;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import bgu.spl.app.PurchaseSchedule;
import bgu.spl.app.messages.NewDiscountBroadcast;
import bgu.spl.app.messages.PurchaseOrderRequest;
import bgu.spl.app.messages.TerminateBroadcast;
import bgu.spl.app.messages.TickBroadcast;
import bgu.spl.mics.MicroService;

/**
 * 
 * WebsiteClientService class represents a Client making Purchases on the store website.
 * a client has a Scheduled Purchase list that according to it, he make it Purchases.
 * client also have a wishlist list which specify what shoe types he would like to buy only on discount. 
 *
 */
public class WebsiteClientService extends MicroService{

	private	List<PurchaseSchedule> purchaseSchedule;
	private	Set<String> wishList;
	private	int currTick;

	/**
	 * 
	 * Comparator implementation for the purpose of sorting  the {@link WebsiteClientService#purchaseSchedule}. 
	 *	
	 */
	class Comp implements Comparator<PurchaseSchedule>{

		public int compare(PurchaseSchedule p1, PurchaseSchedule p2){
			return p1.getPurchaseTick()-p2.getPurchaseTick();
		}
	}

	Comp c = new Comp();

	/**
	 * creates new ManagementService.
	 * 
	 * @param _purchaseSchedule - list of Scheduled purchases.
	 * @param latch - {@link bgu.spl.mics.MicroService}
	 * @param finalLatch - {@link bgu.spl.mics.MicroService}
	 */
	public WebsiteClientService(String name, List<PurchaseSchedule> _purchaseSchedule, Set<String> _wishList, CountDownLatch latch, CountDownLatch finalLatch){ 	
		super(name, latch, finalLatch);
		purchaseSchedule= _purchaseSchedule;
		wishList = _wishList;
		purchaseSchedule.sort(c);//sort the given purchaseSchedule using Comp c.
	}

	/**
	 * {@link bgu.spl.mics.MicroService}
	 */
	protected void initialize(){
		subscribeBroadcast(TerminateBroadcast.class, b -> {terminate();});
		subscribeBroadcast(TickBroadcast.class, b -> {
			this.currTick = b.getCurrTick(); //change time to current time
			if (!purchaseSchedule.isEmpty()){
				while (!purchaseSchedule.isEmpty() && purchaseSchedule.get(0).getPurchaseTick() <= currTick){
					PurchaseOrderRequest purchaseOrderRequest = new PurchaseOrderRequest(this.getName(), purchaseSchedule.get(0).getPurchaseShoeType(), false,  currTick );									
					purchaseSchedule.remove(0);										
					sendRequest(purchaseOrderRequest, req -> {}); 
				}	
			}
		}); 


		subscribeBroadcast(NewDiscountBroadcast.class, b -> {
			if (wishList.contains(b.getShoeTypeToDiscount()) == true){
				String shoeToPurchase = b.getShoeTypeToDiscount();
				PurchaseOrderRequest purchaseOrderRequest = new PurchaseOrderRequest(this.getName(), shoeToPurchase, true,  currTick );
				sendRequest(purchaseOrderRequest, ans -> {
					if (ans != null){//meaning that the purchase was successful and the result is a receipt instead of null.
						wishList.remove(shoeToPurchase);															
					}												
				});
			}

		});
	}

}
