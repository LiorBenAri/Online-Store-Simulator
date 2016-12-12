package bgu.spl.app;

/**
 * 
 * Receipt is a class that represents a detailed Receipt, of a Purchase or Manufacturing transaction. 
 *
 */
public class Receipt {
	
	private	String seller;
	private	String customer;
	private	String shoeType;
	private	boolean discount;
	private	int issuedTick;
	private	int requestTick;
	private	int amountSold;
		
	/**
	 * create a new Receipt, with the given Parameters as it's details. 
	 * 
	 * @param _seller - name of the seller on the transaction.
	 * @param _customer - name of the customer on the transaction.
	 * @param _shoeType - the shoe Type that was Bought/Manufactured on the transaction.
	 * @param _discount - boolean variable to indicate if there was a discount on the transaction.  
	 * @param _issuedTick - the tick number specify when the transaction occur. 
	 * @param _requestTick - the tick number specify when the transaction was requested to be execute.
	 * @param _amountSold - the amount of shoes sold/Manufactured on the transaction.
	 */
		public Receipt(String _seller, String _customer,String _shoeType, boolean _discount,int _issuedTick,int _requestTick,int _amountSold){
			seller = _seller;
			customer = _customer;
			shoeType = _shoeType;
			discount = _discount;
			issuedTick = _issuedTick;
			requestTick = _requestTick;
			amountSold = _amountSold;
		}
		
		/**
		 * 
		 * @return the name of the seller on the transaction.
		 */
		public String getSeller(){
			return seller;
		}
		
		/**
		 * 
		 * @return the name of the customer on the transaction.
		 */
		public String getCustomer(){
			return customer;
		}
		
		/**
		 * 
		 * @return the shoe Type that was Bought/Manufactured on the transaction.
		 */
		public String getShoeType(){
			return shoeType;
		}
		
		/**
		 * 
		 * @return there was a discount on the transaction - true. otherwise - false.  
		 */
		public boolean isOnDiscount(){
			return discount;
		}
		
		/**
		 * 
		 * @return the tick number specify when the transaction occur. 
		 */
		public int getIssuedTick(){
			return issuedTick;
		}
		
		/**
		 * 
		 * @return the tick number specify when the transaction was requested to be execute.
		 */
		public int getRequestTick(){
			return requestTick;
		}
		
		/**
		 * 
		 * @return the amount of shoes sold/Manufactured on the transaction.
		 */
		public int getAmountSold(){
			return amountSold;
		}

}
