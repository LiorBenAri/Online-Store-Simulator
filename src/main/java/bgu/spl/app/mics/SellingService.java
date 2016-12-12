package bgu.spl.app.mics;

import java.util.concurrent.CountDownLatch;

import bgu.spl.app.Receipt;
import bgu.spl.app.Store;
import bgu.spl.app.Store.BuyResult;
import bgu.spl.app.messages.PurchaseOrderRequest;
import bgu.spl.app.messages.RestockRequest;
import bgu.spl.app.messages.TerminateBroadcast;
import bgu.spl.app.messages.TickBroadcast;
import bgu.spl.mics.MicroService;

/**
 * 
 * SellingService class represents a Selling Service of the store.
 * the SellingService handles PurchaseOrderRequest from web client services.
 *
 */
public class SellingService extends MicroService {
	
	private int currTick;
	
	private Store str = Store.getStore();
	
	/**
	 * creates new SellingService.
	 * 
	 * @param name - {@link bgu.spl.mics.MicroService}
	 * @param latch - {@link bgu.spl.mics.MicroService}
	 * @param finalLatch - {@link bgu.spl.mics.MicroService}
	 */
	public SellingService(String name, CountDownLatch latch, CountDownLatch finalLatch){ 	
		super(name, latch, finalLatch);
	}
	
	/**
	 * {@link bgu.spl.mics.MicroService}
	 */
	protected void initialize(){
		subscribeBroadcast(TerminateBroadcast.class, b -> {terminate();});
		subscribeBroadcast(TickBroadcast.class, b -> {this.currTick = b.getCurrTick();});
			
		subscribeRequest(PurchaseOrderRequest.class, req -> {
			BuyResult ans = str.take(req.GetTypeToPurchase(), req.CustomerWantsDiscount());
			
			switch (ans) { 
			case NOT_IN_STOCK: {
				RestockRequest restockRequest = new RestockRequest(getName(), req.GetTypeToPurchase());
				sendRequest(restockRequest,res -> {if (res==false) {//restockRequest didn't succeed.
					                                 complete(req,null);}
												   else {//restockRequest succeed.
													 Receipt receipt = new Receipt(this.getName(), req.GetClientId(), req.GetTypeToPurchase(),
										                     req.CustomerWantsDiscount(), currTick, req.GetRequestedTick(), 1); // creating appropriate receipt
													 str.file(receipt);
													 complete(req, receipt);		
												   }
			});												
				}
			break; 
			case NOT_ON_DISCOUNT: {
				 complete(req, null);
			   	}
			break; 
			case REGULAR_PRICE: {
				Receipt receipt = new Receipt(this.getName(), req.GetClientId(), req.GetTypeToPurchase(),
						                       req.CustomerWantsDiscount(), currTick, req.GetRequestedTick(), 1); // creating appropriate receipt
				str.file(receipt);
				complete(req, receipt);
				}
			break; 
			case DISCOUNTED_PRICE: {
				Receipt receipt = new Receipt(this.getName(), req.GetClientId(), req.GetTypeToPurchase(),
						 true, currTick, req.GetRequestedTick(), 1); // creating appropriate receipt
				str.file(receipt);
				complete(req, receipt);		
				}
			}
	
			});
	}

}
