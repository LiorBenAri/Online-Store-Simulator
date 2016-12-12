package bgu.spl.app;

import java.util.*;

/**
 * 
 * Store is a class that represents a shoe store.
 *
 */
public class Store {
	private ArrayList<ShoeStorageInfo> shoeStorage; //list of all kind of shoes in storage and there properties.
	private	ArrayList<Receipt> ReceiptList; //list of receipts of all the transactions made involving the store.
	
	private Store(){
		shoeStorage = new ArrayList<ShoeStorageInfo>();
		ReceiptList = new ArrayList<Receipt>();
	}
	
	private static class StoreHolder {							
		protected static Store storeInstance = new Store();//creating of the store singleton.
	}
	
	/**
	 *
	 * @return the store singleton. 
	 */
	public static Store getStore() {
		return StoreHolder.storeInstance;
	}
	
	/**
	 * loads storage into the store.  
	 * 
	 * @param storage
	 */
	public void load(ShoeStorageInfo[] storage){
		boolean allreadyExsits = false;
		for(int i=0; i<storage.length; i++){
			ShoeStorageInfo currShoeStoreInfo = storage[i];
			String currShoeType = storage[i].getShoeType();
			allreadyExsits = false;
			for (int j=0; j<shoeStorage.size() && allreadyExsits == false;j++){//checking if the current shoe type loaded to storage
																			   //Already exists on storage.
				if (shoeStorage.get(j).getShoeType()==currShoeType){
					shoeStorage.get(j).setAmountOnStorage(shoeStorage.get(j).getAmountOnStorage()+currShoeStoreInfo.getAmountOnStorage());
					shoeStorage.get(j).setDiscountedAmount(shoeStorage.get(j).getDiscountedAmount()+currShoeStoreInfo.getDiscountedAmount());
					allreadyExsits = true;
					break;
				}
			}
			if (allreadyExsits == false){//the current shoe type loaded to storage is new the storage.
				shoeStorage.add(storage[i]);
			}

		}	
	}
	
	/**
	 * 
	 * defining enum data type to be used on the {@link Store#take(String, boolean)} method.
	 *
	 */
	public enum BuyResult {
		NOT_IN_STOCK,
		NOT_ON_DISCOUNT,
		REGULAR_PRICE,
		DISCOUNTED_PRICE
	}

	/**
	 * 
	 * this method simulates a try to take a shoe from the store. results of this try will be displayed as defined on BuyResult enum.
	 * that's according to the given parameters store's storage info. 
	 * 
	 * @param shoeType - shoe type that is tried to be taken.
	 * @param onlyDiscount - indicate if shoe of {@link shoeType} will be taken only if it on discount.
	 * @return 
	 */
	public synchronized BuyResult take(String shoeType, boolean onlyDiscount){
		ShoeStorageInfo curr_shoe = null;
		 for(int i=0; i<shoeStorage.size() ; i++){              //find shoe type if exists
			 if (shoeStorage.get(i).getShoeType().equals(shoeType)){
				 curr_shoe = shoeStorage.get(i);
				 break;
			 }
		 }
		
		 if (curr_shoe==null){//in case shoe type not in stock
			 if (onlyDiscount){
				 BuyResult ans =  BuyResult.NOT_ON_DISCOUNT;
			 	 return ans; 
			 	}
			 else{
				 shoeStorage.add(new ShoeStorageInfo(shoeType, 0));
				 BuyResult ans =  BuyResult.NOT_IN_STOCK;
			 	 return ans; 
			 }
		 }
		 else if (curr_shoe.getAmountOnStorage()==0){
			 if (onlyDiscount){
				 BuyResult ans =  BuyResult.NOT_ON_DISCOUNT;
			 	 return ans; 
			 	}
			 else{
				 BuyResult ans =  BuyResult.NOT_IN_STOCK;
			 	 return ans; 
			 }
				 
		 }
		 else{//shoe is in stock and amount >0
			 if (onlyDiscount && curr_shoe.getDiscountedAmount()==0){
				 BuyResult ans =  BuyResult.NOT_ON_DISCOUNT;
				 return ans;
			 }
			 else if (curr_shoe.getDiscountedAmount()>0){
				 curr_shoe.setAmountOnStorage(curr_shoe.getAmountOnStorage()-1);
				 curr_shoe.setDiscountedAmount(curr_shoe.getDiscountedAmount()-1);
				 BuyResult ans =  BuyResult.DISCOUNTED_PRICE;
				 return ans; 
			 }
			 else{
				 curr_shoe.setAmountOnStorage(curr_shoe.getAmountOnStorage()-1);
				 BuyResult ans =  BuyResult.REGULAR_PRICE;
				 return ans;
			 }
			 
			 
		 }
	}
	
	/**
	 * given a shoe type and amount parameters, this method adds {@link amount} units of this shoe type to the store. 
	 * 
	 * @param shoeType - the shoe type to be added to the store.
	 * @param amount - the amount of shoes of type {@link shoeType}  to add to the store.
	 */
	public synchronized void add(String shoeType, int amount){
		ShoeStorageInfo curr_shoe = null;
		 for(int i=0; i<shoeStorage.size() ; i++){//find shoe type, if it exists.
			 if (shoeStorage.get(i).getShoeType().equals(shoeType)){
				 curr_shoe = shoeStorage.get(i);
				 break;
			 }
		 }
		 if (curr_shoe!=null){//this shoe type exists on store.
			 curr_shoe.setAmountOnStorage(curr_shoe.getAmountOnStorage()+amount);
		 }
		
	}
	
	/**
	 * given a shoe type and amount parameters, this method adds to {@link amount} units of
	 * {@link shoeType} shoes on the store, discount. 
	 * 
	 * @param shoeType - the shoe type to get more discounted units.
	 * @param amount - the amount of shoes units of type {@link shoeType} to add discount to.
	 */
	public synchronized void addDiscount(String shoeType, int amount){
		ShoeStorageInfo curr_shoe = null;
		 for(int i=0; i<shoeStorage.size() ; i++){//find shoe type, if it exists.
			 if (shoeStorage.get(i).getShoeType().equals(shoeType)){
				 curr_shoe = shoeStorage.get(i);
				 break;
			 }
		 }
		 if (curr_shoe!=null){
			 if (amount > curr_shoe.getAmountOnStorage()-curr_shoe.getDiscountedAmount()){
				 //the amount of shoes units of the given shoeType on storage, with no discount, is smaller then given amount. 
			 	curr_shoe.setDiscountedAmount(curr_shoe.getAmountOnStorage());
			 }
			 else{
				curr_shoe.setDiscountedAmount(curr_shoe.getDiscountedAmount()+amount);
			 }
		}
		
	}
	
	/**
	 * this method adds a transaction (selling/manufacturing) receipt to the receipt list.
	 * 
	 * @param receipt - receipt of a transaction. 
	 */
	public synchronized void file(Receipt receipt){
		ReceiptList.add(receipt);
	}
	
	/**
	 * this method prints the store's storage info, and the store's receipts list.
	 */
	public void print(){
		System.out.println("Shoes in stock-");
		for(int i=0; i<shoeStorage.size() ; i++){
			ShoeStorageInfo curr_shoe = shoeStorage.get(i);
			System.out.println("	Shoe type no."+ i);
			System.out.println("		Name:"+curr_shoe.getShoeType());
			System.out.println("		Amount:"+curr_shoe.getAmountOnStorage());
			System.out.println("		Amount on discount:"+curr_shoe.getDiscountedAmount());
			System.out.println("");

		}

		System.out.println("");
		System.out.println("Receipts-");
		for(int i=0; i<ReceiptList.size() ; i++){
			Receipt curr_receipt = ReceiptList.get(i);
			System.out.println("	Receipt no."+i);
			System.out.println("		Seller: "+curr_receipt.getSeller());
			System.out.println("		Customer: "+curr_receipt.getCustomer());
			System.out.println("		Shoe type: "+curr_receipt.getShoeType());
			System.out.println("		Sold at a discount price: "+curr_receipt.isOnDiscount());
			System.out.println("		Tick in which this receipt was issued: "+curr_receipt.getIssuedTick());
			System.out.println("		Tick in which the customer requested to buy the shoe: "+curr_receipt.getRequestTick());
			System.out.println("		Amount sold: "+curr_receipt.getAmountSold());
			System.out.println("");
		}
	}
}
