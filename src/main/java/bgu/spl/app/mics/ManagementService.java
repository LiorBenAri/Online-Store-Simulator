package bgu.spl.app.mics;
import bgu.spl.mics.MicroService;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import bgu.spl.app.DiscountSchedule;
import bgu.spl.app.Store;
import bgu.spl.app.messages.ManufacturingOrderRequest;
import bgu.spl.app.messages.NewDiscountBroadcast;
import bgu.spl.app.messages.RestockRequest;
import bgu.spl.app.messages.TerminateBroadcast;
import bgu.spl.app.messages.TickBroadcast;

/**
 * 
 * ManagementService class represents the store Management Service.
 * the store Management Service has 2 jobs:
 * 1. sending Discount broadcasts to all the client services.
 * 2. sending Manufacturing requests to factories, when getting a restock requests.
 *
 */
public class ManagementService extends MicroService{
	private int currentTick;
	private List<DiscountSchedule> discountSchedule;//according to this list, the Management will send Discount broadcasts in a chronological order.
	private Map<String,Integer> shoeTypeReStockReserveAmount; //mapping from shoe type to  the amount of that shoe type that already sent to manufecturing.
	private Map<String,Queue<RestockRequest>> waitingForManufacturing; //mapping from shoe type to restock requests of that shoe type, 
																	   //that aren't done and waiting For end of current Manufacturing of this shoe type.
	private final Store store;

	/**
	 * 
	 * Comparator implementation for the purpose of sorting  the {@link ManagementService#discountSchedule}. 
	 *	
	 */
	private class Comp implements Comparator<DiscountSchedule>{

		public int compare(DiscountSchedule ds1, DiscountSchedule ds2){
			return ds1.getDiscountTick()-ds2.getDiscountTick();
		}
	}
	Comp c = new Comp();

	/**
	 * creates new ManagementService.
	 * 
	 * @param discountSchedule - list of Scheduled Discounts.
	 * @param latch - {@link bgu.spl.mics.MicroService}
	 * @param finalLatch - {@link bgu.spl.mics.MicroService}
	 */
	public ManagementService(List<DiscountSchedule> discountSchedule, CountDownLatch latch, CountDownLatch finalLatch){
		super("manager", latch, finalLatch);
		discountSchedule.sort(c);//sort the given discountSchedule using Comp c.
		this.discountSchedule=discountSchedule;
		shoeTypeReStockReserveAmount = new HashMap<String,Integer>();
		waitingForManufacturing = new HashMap<String,Queue<RestockRequest>>();
		store = Store.getStore();		
	}

	@Override
	/**
	 * {@link bgu.spl.mics.MicroService}
	 */
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, b -> {terminate();});
		subscribeBroadcast(TickBroadcast.class, 
				(tickBroadcast) -> 
		{

			currentTick=tickBroadcast.getCurrTick();
			while (!discountSchedule.isEmpty() && discountSchedule.get(0).getDiscountTick() <= currentTick){				
				DiscountSchedule currDiscount = discountSchedule.get(0);
				store.addDiscount(currDiscount.getDiscountShoeType(), currDiscount.getDiscountedAmount());;
				discountSchedule.remove(0);
				NewDiscountBroadcast newDiscountBoradcast = new NewDiscountBroadcast(currentTick, currDiscount.getDiscountShoeType());
				sendBroadcast(newDiscountBoradcast);
			}
		}
				);
		subscribeRequest(RestockRequest.class, 
				restockReq -> {//adding restock request to waiting list
					if	(waitingForManufacturing.containsKey(restockReq.getShoeType())){
						Queue<RestockRequest> shoeTypeWaitingList = waitingForManufacturing.get((restockReq.getShoeType()));
						shoeTypeWaitingList.add(restockReq);
					}
					else{
						Queue<RestockRequest> shoeTypeWaitingList = new LinkedList<RestockRequest>();
						waitingForManufacturing.put(restockReq.getShoeType(), shoeTypeWaitingList);
						shoeTypeWaitingList.add(restockReq);
					}

					if (shoeTypeReStockReserveAmount.containsKey(restockReq.getShoeType()) && //if there are enough shoes of this type in reserve. don't send new manufacturing request.
							shoeTypeReStockReserveAmount.get(restockReq.getShoeType())>0){  
						shoeTypeReStockReserveAmount.put(restockReq.getShoeType(), shoeTypeReStockReserveAmount.get(restockReq.getShoeType())-1);
					}
					else{  //if there are not enough shoes of this kind in Manufacturing, send new Manufacturing request.
						int amountToManufacture = (currentTick%5) + 1;
						shoeTypeReStockReserveAmount.put(restockReq.getShoeType(), amountToManufacture-1); //adds reserve amount weather shoe type already exists or not.
						ManufacturingOrderRequest manufacturingOrderRequest = new ManufacturingOrderRequest(restockReq.getShoeType(), amountToManufacture, currentTick);
						boolean reqAns = sendRequest(manufacturingOrderRequest, receipt -> {
							store.file(receipt);
							Queue<RestockRequest> shoeTypeWaitingList = waitingForManufacturing.get(receipt.getShoeType());
							int amountManugactured = receipt.getAmountSold();
							int amountLeft = amountManugactured;
							while (!shoeTypeWaitingList.isEmpty() && amountLeft>0) {
								RestockRequest currRestockReq = shoeTypeWaitingList.poll(); //restock request to complete
								complete(currRestockReq, true);
								amountLeft--; 		//reserve after restocking, should be added to store												
							}
							store.add(receipt.getShoeType(), amountLeft); //adding remaining reserve to store
							shoeTypeReStockReserveAmount.put(restockReq.getShoeType(), shoeTypeReStockReserveAmount.get(restockReq.getShoeType()) - amountLeft);						

						});
						if (!reqAns){
							complete(restockReq, false);
						}

					}					
				}
				);
	}

}
