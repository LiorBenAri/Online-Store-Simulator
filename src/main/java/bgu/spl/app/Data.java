package bgu.spl.app;
import bgu.spl.app.data.ServicesInput;
import bgu.spl.app.data.ShoeStorageInfoInput;

public class Data {
	
     	ShoeStorageInfoInput[] initialStorage;
	    ServicesInput services;
		
	public 	ShoeStorageInfoInput[] getInitialStorage(){
		return initialStorage;
	}
	
	public ServicesInput getServices(){ 
		return services;
	}	
}
