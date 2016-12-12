package bgu.spl.app;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

import com.google.gson.Gson;

import bgu.spl.app.data.CustomerServiceInput;
import bgu.spl.app.data.DiscountScheduleInput;
import bgu.spl.app.data.ManagerInput;
import bgu.spl.app.data.PurchaseScheduleInput;
import bgu.spl.app.data.ShoeStorageInfoInput;
import bgu.spl.app.data.TimeServiceInput;
import bgu.spl.app.mics.ManagementService;
import bgu.spl.app.mics.SellingService;
import bgu.spl.app.mics.ShoeFactoryService;
import bgu.spl.app.mics.TimeService;
import bgu.spl.app.mics.WebsiteClientService;

/**
 * ShoeStoreRunner contains the main function of the program and runs the program. 
 * it is accepting as argument (command line argument) the name of the json input file to read.
*  ShoeStoreRunner is reading the input file (using Gson), and add the initial storage to
*  the store and create and start the micro-services. at the end of the program run, After all the micro-services
*  terminate themselves, the ShoeStoreRunner is calling the Store�s print function and exit.
*/
public class ShoeStoreRunner {

	private static final Logger logger = Logger.getGlobal();
	
	public static void main(String[] args) {
		
		MyFormat format = new MyFormat();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(format);
		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
		
		
		
			
		List<ShoeFactoryService> factories = new LinkedList<ShoeFactoryService>();
		List<SellingService> sellers = new LinkedList<SellingService>();
		List<WebsiteClientService> customers = new LinkedList<WebsiteClientService>();
		
	
		Store str = Store.getStore();
		Scanner in = new Scanner(System.in);
		System.out.print("Insert file path: ");

		//String path = in.nextLine();
		String path = args[0];
	
	
		
		//===============================jason parser- start===============================================	  
	//	String path = "sample.json";
		

		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(path));
			in.close();
			 Gson gson = new Gson();
			 Data data = gson.fromJson(bufferedReader, Data.class);
			 
			 int TotalThreadAmount = 2 + data.getServices().getFactories() + data.getServices().getSellers() + data.getServices().getCustomers().length;
			 CountDownLatch latch = new CountDownLatch(TotalThreadAmount - 1);
			 CountDownLatch finalLatch = new CountDownLatch(TotalThreadAmount);
			 
			 //-------------------init store --------------------------
			 
			 ShoeStorageInfoInput[] initial_storage_input = data.getInitialStorage();
			 ShoeStorageInfo[] initial_storage = new  ShoeStorageInfo[initial_storage_input.length];
			 for(int i=0; i<initial_storage_input.length; i++){
				  String type = initial_storage_input[i].getShoeType();
				  int amount = initial_storage_input[i].getAmountOnStorage();
				  ShoeStorageInfo curr_shoe = new ShoeStorageInfo(type, amount);	
				  initial_storage[i] = curr_shoe;
			 }
			 
			 str.load(initial_storage);
			
			 //----------------init manager---------------------------
			 
			 List<DiscountSchedule> discount_schedule = new LinkedList<DiscountSchedule>();
			 ManagerInput manager_input = data.getServices().getManager();
			 DiscountScheduleInput[] discount_schedule_input = manager_input.getDiscountSchedule();
			 
			 for(int i=0; i<discount_schedule_input.length; i++){
				 String type = discount_schedule_input[i].getDiscountShoeType();
				 int amount = discount_schedule_input[i].getDiscountedAmount();
				 int tick = discount_schedule_input[i].getDiscountTick();
				 
				  DiscountSchedule curr_discount = new DiscountSchedule(type, tick, amount);
				  discount_schedule.add(curr_discount);				 
			 }
			 
			 ManagementService manager = new ManagementService(discount_schedule,latch, finalLatch);
			 
			 
			//-----------------------init timer---------------------------------------------
			 
			 TimeServiceInput timer_input = data.getServices().getTimeService();
			 int input_speed = timer_input.getSpeed();
			 int input_duration = timer_input.getDuration();
			 TimeService timer = new TimeService(input_speed, input_duration, latch, finalLatch);
			 
			 
			 
			  //--------------init factories---------------------------------------
			  int factory_num = data.getServices().getFactories();
			  for(int i=0; i<factory_num; i++){
			  ShoeFactoryService curr_factory = new ShoeFactoryService("factory "+i, latch, finalLatch);	
			  factories.add(curr_factory);
			  }
			  
			  //-------------init sellers-----------------------------------------
			 
			  int seller_num = data.getServices().getSellers();
			  for(int i=0; i<seller_num; i++){
				  SellingService curr_seller = new SellingService("seller "+i, latch, finalLatch);
				  sellers.add(curr_seller);
			  }
 
			  //------------init customers--------------------------------------
			  			 
			  CustomerServiceInput[] customers_input = data.getServices().getCustomers();
			  for(int i=0; i<customers_input.length; i++){
				  String name_input = customers_input[i].getName();
				  String[] wish_list_input = customers_input[i].getWishList();
				  PurchaseScheduleInput[] purchase_schedul_input =  customers_input[i].getPurchaseSchedule();
				  List<PurchaseSchedule> purchase_schedul = new  LinkedList<PurchaseSchedule>();
				
				  for(int j=0; j<purchase_schedul_input.length; j++){
					  String type = purchase_schedul_input[j].getPurchaseShoeType();
					  int tick = purchase_schedul_input[j].getPurchaseTick();
					  PurchaseSchedule curr_purchase_schedul = new PurchaseSchedule(type, tick);
					  purchase_schedul.add(curr_purchase_schedul);
				  }
				  
				  Set<String> wish_list = new HashSet<String>();
				  for(int j=0; j<wish_list_input.length; j++){
					  wish_list.add(wish_list_input[j]);					  
				  }
				  
				  WebsiteClientService curr_client = new WebsiteClientService(name_input, purchase_schedul, wish_list , latch, finalLatch);
				  customers.add(curr_client);		
				  
					//---------------------------jason parser- end----------------------------------------------------------------------
					
			
			  }		
		
			  ExecutorService e = Executors.newFixedThreadPool(TotalThreadAmount);
				
			  logger.log(Level.INFO, "----------------------Start of simulation----------------------");	
				for(int i=0;i<factories.size();i++) {
			           e.execute(factories.get(i));
			        }
				
				for(int i=0;i<sellers.size();i++) {
			           e.execute(sellers.get(i));
			        }
				
				for(int i=0;i<customers.size();i++) {
			           e.execute(customers.get(i));
			        }
				
				e.execute(manager);
				e.execute(timer);//need to init timer
				e.shutdown();
				
				try {
					finalLatch.await();
					str.print();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			
				
				
		//==============================================================================================		
			  		  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}	
}
