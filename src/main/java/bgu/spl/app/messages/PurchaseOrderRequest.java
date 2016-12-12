package bgu.spl.app.messages;
import bgu.spl.app.Receipt;
import bgu.spl.mics.Request;

/**
 * 
 * PurchaseOrderRequest class represents a single Purchase Request.
 *
 */
public class PurchaseOrderRequest implements Request<Receipt> {
	
		private String clientId;
		private String typeToPurchase;
		private int requestedTick;//the tick number that specify when the Purchase Request was made. 
		private boolean costumerWantsOnDiscount; //true - if customer wants to buy only with a discount. else - false.

		/**
		 * creates new PurchaseOrderRequest. 
		 * 
		 * @param _senderId - name of the requester of the PurchaseOrderRequest.
		 * @param _typeToPurchase - shoe type the requester wants to purchase.
		 * @param _costumerWantsOnDiscount - indicate if customer wants to buy only with a discount.
		 * @param _requestedTick - the tick number that specify when the Purchase Request was made.
		 */
		public PurchaseOrderRequest(String _senderId,String _typeToPurchase, boolean _costumerWantsOnDiscount, int _requestedTick){
			clientId = _senderId;
			typeToPurchase = _typeToPurchase;
			costumerWantsOnDiscount = _costumerWantsOnDiscount;
			requestedTick = _requestedTick;	
		}
	
		/**
		 * 
		 * @return the name of the requester of the PurchaseOrderRequest.
		 */
		public String GetClientId(){
			return clientId;
		}
		

		/**
		 * 
		 * @return the shoe type the requester wants to purchase.
		 */
		public String GetTypeToPurchase(){
			return typeToPurchase;
		}
		
		/**
		 * 
		 * @return the tick number that specify when the Purchase Request was made.
		 */
		public int GetRequestedTick(){
			return requestedTick;
		}
		
		public boolean CustomerWantsDiscount(){
			return costumerWantsOnDiscount;
		}
}
