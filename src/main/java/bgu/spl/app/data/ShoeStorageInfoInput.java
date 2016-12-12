package bgu.spl.app.data;

import bgu.spl.app.ShoeStorageInfo;

/**
 * 
 * ShoeStoreInfoInput is a class corresponding to the class {@link ShoeStorageInfo}, 
 * which responsible of handling json input of type ShoeStorageInfo.
 *
 */
public class ShoeStorageInfoInput {
	
	private	String shoeType;
	private	int amount;
	private	int discountedAmount;
	
	/**
	 * {@link ShoeStorageInfo#ShoeStorageInfo(String, int)}. 
	 */
	public ShoeStorageInfoInput(String _shoeType,int _amountOnStorage){
		shoeType = _shoeType;
		amount = _amountOnStorage;
		discountedAmount = 0;
	}
	
	/**
	 * {@link ShoeStorageInfo#getShoeType()}. 
	 */
	public String getShoeType(){
		return shoeType;
	}
	
	/**
	 * {@link ShoeStorageInfo#getAmountOnStorage()}. 
	 */
	public int getAmountOnStorage(){
		return amount;
	}
	

	/**
	 * {@link ShoeStorageInfo#setAmountOnStorage(int)}. 
	 */
	public void setAmountOnStorage(int _amountOnStorage){
		amount = _amountOnStorage;
	}
	

	/**
	 * {@link ShoeStorageInfo#getDiscountedAmount()}. 
	 */
	public int getDiscountedAmount(){
		return discountedAmount;
	}
	

	/**
	 * {@link ShoeStorageInfo#setDiscountedAmount(int)}. 
	 */
	public void setDiscountedAmount(int _discountedAmount){
		discountedAmount = _discountedAmount;
	}
}
