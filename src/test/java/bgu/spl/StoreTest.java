package bgu.spl;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import bgu.spl.app.Receipt;
import bgu.spl.app.ShoeStorageInfo;
import bgu.spl.app.Store;
import bgu.spl.app.Store.BuyResult;

public class StoreTest {
	private Store store;
	private final String shoeType1 = "type1"; 
	private ShoeStorageInfo info1 = new ShoeStorageInfo(shoeType1, 1);
	private ShoeStorageInfo[] infoArray = {info1};

	public Store createStore() {
		return Store.getStore();
	}
		
	@Before
	public void setUp() throws Exception {
		store = createStore();
	}


	@After
	public void tearDown() throws Exception {
		store.take(shoeType1, false);
	}

	
	@Test
	public void testGetStore() {
		assertEquals(Store.getStore(), store);
	}
	
	@Test
	public void testLoad() {
		assertTrue(store.take(shoeType1, false)==BuyResult.NOT_IN_STOCK);
		//"type 1" shoes are loaded to storage.
		store.load(infoArray);
		assertTrue(store.take(shoeType1, false)==BuyResult.REGULAR_PRICE);
	}
	
	
	@Test
	public void testTake() {		
		//trying to take "type1" shoe, that is written on storage, but isn't on stock.
		assertTrue(store.take("type1", false)==BuyResult.NOT_IN_STOCK);

		//trying to take (only on discount) "type1" shoe, that is on stock.
		store.load(infoArray);

		//test case 1 -"type1" shoe is not on discount.
		assertTrue(infoArray[0].getDiscountedAmount()==0);
		assertTrue(store.take(shoeType1, true)==BuyResult.NOT_ON_DISCOUNT);
		//check that the only "type1" shoe on stock, wasn't taken, because there is no discount on this shoe.
		assertFalse(store.take(shoeType1, true)==BuyResult.NOT_IN_STOCK);
		try {
			tearDown();
		} catch (Exception e) {}

		//test case 2 -"type1" shoe is discounted.
		infoArray[0].setDiscountedAmount(1);
		int currDiscountedAmount = infoArray[0].getDiscountedAmount();
		assertEquals(currDiscountedAmount,1);
		store.load(infoArray);
		assertTrue(store.take(shoeType1, true)==BuyResult.DISCOUNTED_PRICE);
		//check that the only "type1" shoe on stock, was taken, and now is out off stock.
		assertTrue(store.take(shoeType1, false)==BuyResult.NOT_IN_STOCK);//maybe i'll fix it later. 

		//trying to take "type1" shoe, that is on stock.
		infoArray[0].setDiscountedAmount(0);
		currDiscountedAmount = infoArray[0].getDiscountedAmount();
		assertEquals(currDiscountedAmount,0);
		store.load(infoArray);
		assertTrue(infoArray[0].getAmountOnStorage()==1);
		assertTrue(store.take(shoeType1, false)==BuyResult.REGULAR_PRICE);
		//check that the only "type1" shoe on stock, was taken, and now is out off stock.
		assertTrue(store.take(shoeType1, false)==BuyResult.NOT_IN_STOCK);
	}
	
	
	@Test
	public void testAdd() {
		//"type 1" shoes are added to storage.

		//test case 1 - "type 1" shoes weren't on storage before executing add method. 
		assertTrue(store.take(shoeType1, false)==BuyResult.NOT_IN_STOCK);
		store.add(shoeType1, 1);
		assertTrue(store.take(shoeType1, false)==BuyResult.REGULAR_PRICE);
		try {
			tearDown();
		} catch (Exception e) {}

		//test case 2 - "type 1" shoes were on storage before executing add method.
		store.load(infoArray);
		store.add(shoeType1, 1);
		assertTrue(store.take(shoeType1, false)==BuyResult.REGULAR_PRICE);
		assertTrue(store.take(shoeType1, false)==BuyResult.REGULAR_PRICE);
		try {
			this.tearDown();
		} catch (Exception e) {}
		//juint will make another automatic teardown.
	}
	
	@Test
	public void testAddDiscount() {
		store.load(infoArray); //infoArray[0]=ShoeStoreInfo(shoeType1, 1);
		//the "type1" shoes on storage aren't discounted.   
		assertTrue(infoArray[0].getDiscountedAmount()==0);
		assertTrue(store.take(shoeType1, true)==BuyResult.NOT_ON_DISCOUNT);
		//adding discount to the "type1" shoes on storage.
		store.addDiscount(shoeType1, 1);
		assertTrue(store.take(shoeType1, true)==BuyResult.DISCOUNTED_PRICE);
	}
	
	@Test
	public void testFile() {
		//there are no receipts on the store's receipts list.
		System.out.println("check if the output is as expected before adding receipt to the store:");
		System.out.println("");
		System.out.println("expected print for no receipts on the store's receipts list:");
		System.out.println("Shoes in stock-");
		System.out.println("	Shoe type no.0");
		System.out.println("		Name:type1");
		System.out.println("		Amount:0");
		System.out.println("		Amount on discount:0");
		System.out.println("");
		System.out.println("");
		System.out.println("Receipts-");
		System.out.println("");

		System.out.println("print() method output for that case:");
		store.print();
		
		System.out.println("");
		System.out.println("the last two outputs were the same?");
		//Save the old System.out!
		PrintStream old = System.out;

		// Create a stream to hold the output
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos1);
		// Tell Java to use your special stream
		System.setOut(ps);
		// Print some output: goes to your special stream
		System.out.println("Shoes in stock-");
		System.out.println("	Shoe type no.0");
		System.out.println("		Name:type1");
		System.out.println("		Amount:0");
		System.out.println("		Amount on discount:0");
		System.out.println("");
		System.out.println("");
		System.out.println("Receipts-");

		// Create a stream to hold the output
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		ps = new PrintStream(baos2);
		// Tell Java to use your special stream
		System.setOut(ps);
		//Print some output: goes to your special stream
		store.print();

		System.out.flush();
		System.setOut(old);
				
		byte[] bA1 = baos1.toByteArray();
		
		byte[] bA2 = baos2.toByteArray();
		
		assertTrue("false", (Arrays.equals(bA1, bA2)));
		
		System.out.println((Arrays.equals(bA1, bA2)));
		
		store.file(new Receipt("seller", "customer", "Type1", false, 2, 1, 1));
		
		//there is new and single receipt on the store's receipts list.
		System.out.println("");
		System.out.println("check that the output changes due to the adding of receipt to the store's receipts list:");
		
		System.out.println("expected print for new and single receipt on the store's receipts list:");
		System.out.println("Shoes in stock-");
		System.out.println("	Shoe type no.0");
		System.out.println("		Name:type1");
		System.out.println("		Amount:0");
		System.out.println("		Amount on discount:0");
		System.out.println("");
		System.out.println("");
		System.out.println("Receipts-");
		System.out.println("	Receipt no.0");
		System.out.println("		Seller: seller");
	    System.out.println("		Customer: customer");
	    System.out.println("		Shoe type: Type1");
	    System.out.println("		Sold at a discount price: false");
	    System.out.println("		Tick in which this receipt was issued: 2");   
	    System.out.println("		Tick in which the customer requested to buy the shoe: 1");
	    System.out.println("		Amount sold: 1");
	    System.out.println("");
	           
		System.out.println("print() method output for that case:");
		store.print();
		
		System.out.println("the two last outputs the were same?");
		
		//Save the old System.out!

		// Create a stream to hold the output
		ps = new PrintStream(baos1);
		// Tell Java to use your special stream
		System.setOut(ps);
		// Print some output: goes to your special stream
		System.out.println("Shoes in stock-");
		System.out.println("	Shoe type no.0");
		System.out.println("		Name:type1");
		System.out.println("		Amount:0");
		System.out.println("		Amount on discount:0");
		System.out.println("");
		System.out.println("");
		System.out.println("Receipts-");
		System.out.println("	Receipt no.0");
		System.out.println("		Seller: seller");
	    System.out.println("		Customer: customer");
	    System.out.println("		Shoe type: Type1");
	    System.out.println("		Sold at a discount price: false");
	    System.out.println("		Tick in which this receipt was issued: 2");   
	    System.out.println("		Tick in which the customer requested to buy the shoe: 1");
	    System.out.println("		Amount sold: 1");
	    System.out.println("");

		// Create a stream to hold the output
		ps = new PrintStream(baos2);
		// Tell Java to use your special stream
		System.setOut(ps);
		//Print some output: goes to your special stream
		store.print();

		System.out.flush();
		System.setOut(old);
		
		bA1 = baos1.toByteArray();

		bA2 = baos2.toByteArray();
		
		assertTrue("false", (Arrays.equals(bA1, bA2)));	
		
		System.out.println((Arrays.equals(bA1, bA2)));
	}


}