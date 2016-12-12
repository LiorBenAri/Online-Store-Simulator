package bgu.spl.app.messages;
import bgu.spl.app.Receipt;
import bgu.spl.mics.Request;

/**
 * 
 * ManufacturingOrderRequest class represents a Manufacturing Request sent to a factory.
 *
 */
public class ManufacturingOrderRequest implements Request<Receipt>{
	
	private String typeToManufacture;
	private int amount;
	private int tickTheRequestWasMade;

	/**
	 * given a shoe type, amount, and the tick when the Manufacturing Request Was Made, create new ManufacturingOrderRequest
	 * According to this parameters.
	 * 
	 * @param _typeToManufacture - the shoe type requested to manufacture.
	 * @param _amount - the amount of shoes of type {@link _typeToManufacture} requested to manufacture.
	 * @param _tickTheRequestWasMade - the tick when the Manufacturing Request Was Made.
	 */
	public ManufacturingOrderRequest(String _typeToManufacture, int _amount, int _tickTheRequestWasMade){
		typeToManufacture = _typeToManufacture;
		amount = _amount;
		tickTheRequestWasMade = _tickTheRequestWasMade;
	}
	
	/**
	 * 
	 * @return the shoe type requested to manufacture.
	 */
	public String GetTypeToManufacture(){
		return typeToManufacture;
	}
	
	/**
	 * 
	 * @return the amount of shoes of type {@link ManufacturingOrderRequest#typeToManufacture} requested to manufacture.
	 */
	public int GetAmount(){
		return amount;
	}
	
	/**
	 * Decrease by 1 the amount of shoes that were asked to Manufacture.
	 * for internal use of the factory to indicate how many shoes already been created for this request by the factory
	 */
	public void decAmount(){
		amount = amount - 1 ;
	}
	
	/**
	 * 
	 * @return the tick when the Manufacturing Request Was Made.
	 */
	public int getTickTheRequestWasMade(){
		return tickTheRequestWasMade;
	}


}
