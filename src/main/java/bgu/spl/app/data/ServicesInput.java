package bgu.spl.app.data;


public class ServicesInput {
	
	private TimeServiceInput time;
	private ManagerInput manager;
	private int factories;
	private int sellers;
	private CustomerServiceInput[] customers;
	
	public TimeServiceInput getTimeService(){
		return time;
	}
	
	public ManagerInput getManager(){
		return manager;
	}
	
	public int getFactories(){
		return factories;
	}
	
	public int getSellers(){
		return sellers;
	}
	
	public CustomerServiceInput[]  getCustomers(){
		return customers;
	}

}
