package bgu.spl.app.mics;
import bgu.spl.app.Receipt;
import bgu.spl.app.messages.ManufacturingOrderRequest;
import bgu.spl.app.messages.TerminateBroadcast;
import bgu.spl.app.messages.TickBroadcast;
import bgu.spl.mics.MicroService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * 
 * ShoeFactoryService class represents a Shoe Factory Service.
 * the ShoeFactoryService handles ManufacturingOrderRequest from the ManagementService.
 *
 */
public class ShoeFactoryService extends MicroService{

	private int currTick;

	Queue<ArrayList<ManufacturingOrderRequest>> pendingRequests = new LinkedList<ArrayList<ManufacturingOrderRequest>>();
	//each link is an array list with a size of 2 s.t in index 0 is the original request
	Map<ManufacturingOrderRequest, Integer> reqToLastManufectured = new HashMap<ManufacturingOrderRequest, Integer>();
	//key is a pending request for manufacturing, value is the tick a shoe was last created from the request.


	/**
	 * creates new ShoeFactoryService.
	 * 
	 * @param name - {@link bgu.spl.mics.MicroService}
	 * @param latch - {@link bgu.spl.mics.MicroService}
	 * @param finalLatch - {@link bgu.spl.mics.MicroService}
	 */	
	public ShoeFactoryService(String name,  CountDownLatch latch, CountDownLatch finalLatch){
		super(name, latch, finalLatch);
	}

	/**
	 * {@link bgu.spl.mics.MicroService}
	 */
	protected void initialize(){
		subscribeBroadcast(TerminateBroadcast.class, b -> {terminate();});
		subscribeBroadcast(TickBroadcast.class, b -> {
			this.currTick = b.getCurrTick();
			if (!pendingRequests.isEmpty()){//if there are still Manufacturing Requests.
				ArrayList<ManufacturingOrderRequest> currReq = pendingRequests.peek();
				if (currReq.get(1).GetAmount()==0){//all shoes were created, completes request.
					Receipt receipt = new Receipt(this.getName(), "store", currReq.get(0).GetTypeToManufacture(), false, currTick, currReq.get(0).getTickTheRequestWasMade(), currReq.get(0).GetAmount());
					pendingRequests.remove();
					reqToLastManufectured.remove(currReq.get(0));
					complete(currReq.get(0), receipt);
				}
				else{//in case there are pending requests that are not complete.

					if (reqToLastManufectured.get(currReq.get(0)) <= currTick-1){
						currReq.get(1).decAmount();
						reqToLastManufectured.put(currReq.get(0), currTick);	
					}


				}
			}	
		}); 

		subscribeRequest(ManufacturingOrderRequest.class, req -> {
			ManufacturingOrderRequest monitorReq = new ManufacturingOrderRequest(req.GetTypeToManufacture(), req.GetAmount(), req.getTickTheRequestWasMade());
			ArrayList<ManufacturingOrderRequest> pairOfRequests = new ArrayList<ManufacturingOrderRequest>();			
			pairOfRequests.add(req);
			pairOfRequests.add(monitorReq);		
			pendingRequests.add(pairOfRequests);
			reqToLastManufectured.put(req, currTick);
		});//adds the request to the pending requests queue.

	}

}
