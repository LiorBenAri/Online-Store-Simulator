package bgu.spl;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.app.PurchaseSchedule;
import bgu.spl.app.messages.PurchaseOrderRequest;
import bgu.spl.app.messages.TickBroadcast;
import bgu.spl.app.mics.SellingService;

import bgu.spl.app.mics.WebsiteClientService;
import bgu.spl.mics.Message;
import bgu.spl.mics.RequestCompleted;
import bgu.spl.mics.impl.MessageBusImpl;

public class MessageBusImplTest {//creating objects to use in the tests.
	private MessageBusImpl messageBus;
	CountDownLatch latch = new CountDownLatch(0);
	CountDownLatch finalLatch = new CountDownLatch(0); 

	private SellingService seller;

	PurchaseSchedule Ps = new PurchaseSchedule("type1", 1);
	PurchaseOrderRequest pS1 = new PurchaseOrderRequest("client", "type1", false, 1);
	List<PurchaseSchedule> purchaseSchedule = new LinkedList<PurchaseSchedule>();
	Set<String> wishList;
	private WebsiteClientService client;



	private MessageBusImpl createMessageBus() {
		return MessageBusImpl.getMessageBus();
	}
	
	@Before
	public void setUp() throws Exception {//creating objects to use in the tests.
		messageBus = createMessageBus();

		seller = new SellingService("seller", latch, finalLatch);
		messageBus.register(seller);
		messageBus.subscribeRequest(PurchaseOrderRequest.class, seller);
		messageBus.subscribeBroadcast(TickBroadcast.class, seller);

		purchaseSchedule.add(Ps);
		client = new WebsiteClientService("client", purchaseSchedule, wishList, latch, finalLatch);
		messageBus.register(client);
		messageBus.subscribeBroadcast(TickBroadcast.class, client);
	}
	
	@After
	public void tearDown() throws Exception {
		messageBus.unregister(seller);
		messageBus.unregister(client);
	}

	@Test
	public void testRegister() {
		testHelper3(pS1);
	}
	

	@Test
	public void testUnregister(){//on setup method, seller is being registered 
								//and subscribes to PurchaseOrderRequest and TickBroadcast.

		//before Unregister method "seller" is registered.
		testRegister();

		//checks if after executing the Unregister method on micro-service.
		//the micro-service has no queue, and no references to it on the messagebus.

		//test case 1 - after Unregister method "seller" has no queue for messages. 
		testHelper1();

		//test case 2 - after Unregister method "seller" has no references to it on the messagebus.
		testHelper2();
	}
	
	private void testHelper1(){//check that after Unregister method "seller" has no queue for messages.

		boolean thrown = false;
		messageBus.unregister(seller);

		try {
			messageBus.awaitMessage(seller);
		} catch (IllegalStateException | InterruptedException e) 
		{
			thrown = true;
		}
		assertTrue(thrown);
	}

	private void testHelper2(){//check that after Unregister method "seller" has no references to it on the messagebus.
		messageBus.unregister(seller);
		boolean foundSeller;
		foundSeller = messageBus.sendRequest(pS1, client);
		assertFalse(foundSeller);
	}

	private void testHelper3(Message message1){
		//check if this method indeed Register a given micro-service and initialize a messages queue for this micro services.
		//Registration is done on setup.
		Message mesaage2 = null; 
		if (TickBroadcast.class.isAssignableFrom(message1.getClass())){
			messageBus.sendBroadcast((TickBroadcast)message1);
			try {
				mesaage2 = (TickBroadcast) messageBus.awaitMessage(seller);//the awaitMessage method is pulling a message from a registered micro-service queue. 
			} catch (InterruptedException e) {}	
		}
		else if(PurchaseOrderRequest.class.isAssignableFrom(message1.getClass())){
			messageBus.sendRequest((PurchaseOrderRequest)message1, client);
			try {
				mesaage2 = (PurchaseOrderRequest) messageBus.awaitMessage(seller);//the awaitMessage method is pulling a message from a registered micro-service queue. 
			} catch (InterruptedException e) {}	
		}
		
		else if(RequestCompleted.class.isAssignableFrom(message1.getClass())){
			try {
				mesaage2 = (RequestCompleted) messageBus.awaitMessage(seller);//the awaitMessage method is pulling a message from a registered micro-service queue. 
			} catch (InterruptedException e) {}	
		}
		
		//on the two first cases we are sending a message that should be added to micro-service "seller" queue (on the last case 
		//RequestCompleted message is added as part of the complete method).
		//if the message will be added to a micro-service queue, it means that micro-service "seller" 
		//has been registered and his messages queue has been initialized. 
							 
		assertEquals(message1, mesaage2);//passing this test means that indeed the message was added to the a registered micro-service ("seller") queue. meaning that
		//the register process of the "seller" has succeed and a new queue was allocated for it.	
	}

	
	@Test
	public void testAwaitMessage() {
		testHelper3(pS1);
		testHelper1();
	}

	
	@Test
	public void testSubscribeRequest() {
		//Subscription is done on setup method.
		testRegister();
		testHelper2();
	}
	
	
	@Test
	public void testSubscribeBroadcast() {
		testHelper3(new TickBroadcast(1));	
	}
	
	
	@Test
	public void testSendBroadcast() {
		testHelper3(new TickBroadcast(1));
	}
	
	@Test
	public void testSendRequest() {
		testHelper3(pS1);
	}
	
	
	@Test
	public void testComplete() {
		testHelper3(pS1);
		messageBus.complete(pS1, null);
		try {
			assertTrue(RequestCompleted.class.isAssignableFrom(messageBus.awaitMessage(client).getClass()));
		} catch (InterruptedException e) {}
	}
	 
}