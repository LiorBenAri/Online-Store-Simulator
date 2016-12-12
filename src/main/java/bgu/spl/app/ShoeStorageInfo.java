package bgu.spl.app;

/**
 * 
 * ShoeStorageInfo is a class that represents a specific storage info of a specific shoeType.   
 *
 */
public class ShoeStorageInfo{
	
	private	String shoeType;
	private	int amountOnStorage;
	private	int discountedAmount;
	
	/**
	 * Given a shoe type and amount parameters, Creates a new ShoeStorageInfo. discountedAmount is initialized to 0.  
	 * 
	 * @param _shoeType - the ShoeStorageInfo shoe type.
	 * @param _amountOnStorage - the amount of shoes of type {@link shoeType} on this specific ShoeStorageInfo. 
	 */
	public ShoeStorageInfo(String _shoeType,int _amountOnStorage){
		shoeType = _shoeType;
		amountOnStorage = _amountOnStorage;
		discountedAmount = 0;
	}
	
	/**
	 * 
	 * @return the ShoeStorageInfo shoe type.
	 */
	public String getShoeType(){
		return shoeType;
	}
	
	/**
	 * 
	 * @return the amount of shoes of type {@link shoeType} on this specific ShoeStorageInfo.
	 */
	public int getAmountOnStorage(){
		return amountOnStorage;
	}
	
	/**
	 * updates the amount of shoes of type {@link shoeType} on this specific ShoeStorageInfo to be {@link _amountOnStorage}.
	 * 
	 * @param _amountOnStorage - amount of shoes to update the {@link ShoeStorageInfo#amountOnStorage} to.
	 */
	public void setAmountOnStorage(int _amountOnStorage){
		amountOnStorage = _amountOnStorage;
	}
	
	/**
	 * 
	 * @return the Amount of shoes on discount on the {@link ShoeStorageInfo}.
	 */
	public int getDiscountedAmount(){
		return discountedAmount;
	}


	/**
	 * updates the amount of discounted shoes of type {@link shoeType} on this specific {@link ShoeStorageInfo} to be {@link _discountedAmount}.
	 * 
	 * @param _amountOnStorage - amount of shoes to update the {@link ShoeStorageInfo#discountedAmount} to.
	 */
	public void setDiscountedAmount(int _discountedAmount){
		discountedAmount = _discountedAmount;
	}
}
