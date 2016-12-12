package bgu.spl.app.messages;
import bgu.spl.mics.Request;

/**
 * 
 * RestockRequest class represents a single Restock Request.
 *
 */
public class RestockRequest implements Request<Boolean>{
	
	private String sellingServiceId; //selling service requesting the re-stock
	private String shoeType;
		
	/**
	 * creates new RestockRequest. 
	 * 
	 * @param _sellingServiceId - name of the selling service how requested the RestockRequest.
	 * @param _shoeType - the shoe type the selling service requested the RestockRequest.
	 */
		public RestockRequest(String _sellingServiceId, String _shoeType){
			sellingServiceId = _sellingServiceId;		
			shoeType = 	_shoeType;
		}
		
		/**
		 * 
		 * @return name of the selling service how requested the RestockRequest.
		 */
		public String getSelllerId(){
			return sellingServiceId;
		}
		
		/**
		 * 
		 * @return the shoe type the selling service requested the RestockRequest.
		 */
		public String getShoeType(){
			return shoeType;
		}


}
