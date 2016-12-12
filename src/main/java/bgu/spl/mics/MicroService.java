package bgu.spl.mics;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import bgu.spl.app.messages.TickBroadcast;
import bgu.spl.mics.impl.MessageBusImpl;

/**
 * The MicroService is an abstract class that any micro-service in the system
 * must extend. The abstract MicroService class is responsible to get and
 * manipulate the singleton {@link MessageBus} instance.
 * <p>
 * Derived classes of MicroService should never directly touch the message-bus.
 * Instead, they have a set of internal protected wrapping methods (e.g.,
 * {@link #sendBroadcast(bgu.spl.mics.Broadcast)}, {@link #sendBroadcast(bgu.spl.mics.Broadcast)},
 * etc.) they can use . When subscribing to message-types,
 * the derived class also supplies a {@link Callback} that should be called when
 * a message of the subscribed type was taken from the micro-service
 * message-queue (see {@link MessageBus#register(bgu.spl.mics.MicroService)}
 * method). The abstract MicroService stores this callback together with the
 * type of the
 * message is related to.
 * <p>
 */
public abstract class MicroService implements Runnable {
	
	protected boolean terminated = false;
    private final String name;
    protected final MessageBus msgBus = MessageBusImpl.getMessageBus();
    protected Map<Class<? extends Request>,Callback> requestTypeTotCallback;
    protected Map<Class<? extends Broadcast>,Callback> broadcastTypeTotCallback;
    protected Map<Request,Callback> requestToOnComplete;
    protected CountDownLatch latch;
    protected CountDownLatch finalLatch;
    protected static final Logger logger = Logger.getGlobal();
	      
    /**
     * 
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     * @param latch - a CountDownLatch, used for making sure that all services initializing and register before the time service does the same.
	 * @param finalLatch - each thread beside the main thread will count down this latch after unregistering, until this happens, the main thread will wait and on its release will invoke the store's print function.
     */
    
    public MicroService(String name,  CountDownLatch latch,CountDownLatch finalLatch ) { 
        this.name = name;
        requestTypeTotCallback = new HashMap<Class<? extends Request>,Callback>();
        broadcastTypeTotCallback = new HashMap<Class<? extends Broadcast>,Callback>();
        requestToOnComplete = new HashMap<Request,Callback>();
        this.latch = latch;
        this.finalLatch = finalLatch;
        
    }
    /**
     * 
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public MicroService(String name) { 
        this.name = name;
        requestTypeTotCallback = new HashMap<Class<? extends Request>,Callback>();
        broadcastTypeTotCallback = new HashMap<Class<? extends Broadcast>,Callback>();
        requestToOnComplete = new HashMap<Request,Callback>();
        
    }

    /**
     * subscribes to requests of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. subscribe to requests in the singleton event-bus using the supplied
     * {@code type}
     * 2. store the {@code callback} so that when requests of type {@code type}
     * received it will be called.
     * <p>
     * for a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <R>      the type of request to subscribe to
     * @param type     the {@link Class} representing the type of request to
     *                 subscribe to.
     * @param callback the callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     */
    protected final <R extends Request> void subscribeRequest(Class<R> type, Callback<R> callback) {  	
    	msgBus.subscribeRequest(type, this);
    	requestTypeTotCallback.put(type, callback);
    }

    /**
     * subscribes to broadcast message of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. subscribe to broadcast messages in the singleton event-bus using the
     * supplied {@code type}
     * 2. store the {@code callback} so that when broadcast messages of type
     * {@code type} received it will be called.
     * <p>
     * for a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <B>      the type of broadcast message to subscribe to
     * @param type     the {@link Class} representing the type of broadcast
     *                 message to
     *                 subscribe to.
     * @param callback the callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     */
    protected final <B extends Broadcast> void subscribeBroadcast(Class<B> type, Callback<B> callback) {
    	msgBus.subscribeBroadcast(type, this);
    	broadcastTypeTotCallback.put(type, callback);
       }
       		
        		
    /**
     * send the request {@code r} using the message-bus and storing the
     * {@code onComplete} callback so that it will be executed <b> in this
     * micro-service event loop </b> once the request is complete.
     * <p>
     * @param <T>        the type of the expected result of the request
     *                   {@code r}
     * @param r          the request to send
     * @param onComplete the callback to call when {@code r} is completed. This
     *                   callback expects to receive (i.e., in the
     *                   {@link Callback#call(java.lang.Object)} first argument)
     *                   the result provided when the micro-service receiving {@code r} completes
     *                   it.
     * @return true if there was at least one micro-service subscribed to
     *         {@code r.getClass()} and false otherwise.
     */
    protected final <T> boolean sendRequest(Request<T> r, Callback<T> onComplete) {
    	
    	logger.log(Level.INFO, this.getName() + " sent " + r.getClass().getSimpleName());
    	requestToOnComplete.putIfAbsent(r, onComplete);
    	return msgBus.sendRequest(r, this);
    	
    }

    /**
     * send the broadcast message {@code b} using the message-bus.
     * <p>
     * @param b the broadcast message to send
     */
    protected final void sendBroadcast(Broadcast b) {
    	if (TickBroadcast.class.isAssignableFrom(b.getClass())){
    		TickBroadcast tick = (TickBroadcast) b;
    		if (tick.getCurrTick()<61){
    		logger.log(Level.INFO, "Current tick: " + tick.getCurrTick());
    		}
    		else{
    			logger.log(Level.INFO, "End of simulation");
    		}
    	}
    	else{
    		logger.log(Level.INFO, this.getName() + " sent " + b.getClass().getSimpleName());
    	}
        msgBus.sendBroadcast(b);
    }
    
    /**
     * complete the received request {@code r} with the result {@code result}
     * using the message-bus.
     * <p>
     * @param <T>    the type of the expected result of the received request
     *               {@code r}
     * @param r      the request to complete
     * @param result the result to provide to the micro-service requesting
     *               {@code r}.
     */
    protected final <T> void complete(Request<T> r, T result) {
    	logger.log(Level.INFO, this.getName() + " completed " + r.getClass().getSimpleName());
        msgBus.complete(r, result);
    }
    
    /**
     * unregister this MicroService.
     */
    protected final void unregister() {
        msgBus.unregister(this);
    }

    /**
     * this method is called once when the event loop starts.
     * this method is taking care of services subscriptions. 
     */
    protected abstract void initialize();

    /**
     * signal the event loop that it must terminate after handling the current
     * message.
     */
    protected final void terminate() {
        this.terminated = true;
    }

    /**
     * @return the name of the service - the service name is given to it in the
     *         construction time and is used mainly for debugging purposes.
     */
    public final String getName() {
        return name;
    }

    /**s
     * the entry point of the micro-service.
     * register the micro-service and then starts it event loop.  
     */
    
    public final void run() {   	
    	msgBus.register(this);
    	initialize();   
    	this.latch.countDown();
    	logger.log(Level.INFO, this.getName() + " is initialized");
        while (!terminated) {
            Message m;
			try {
				
				m = msgBus.awaitMessage(this);
				 if (RequestCompleted.class.isAssignableFrom(m.getClass())){ //in case of message of type MessageCompleted						 						 
					 RequestCompleted r = (RequestCompleted)m;
					 Callback c = requestToOnComplete.get(r.getCompletedRequest());
					 c.call(r.getResult());	
					 
	
				 }
				 else {		
					 Callback c = getCallback(m);
					 c.call(m);
					 
				 }
				
				
			} catch (InterruptedException e) {}
			
        }
        unregister();
        this.finalLatch.countDown();       
    }
    
    /**
     * 
     * @param - given message.
     * @return -appropriate callBack according to the given message.
     */
    private Callback getCallback(Message m){
    	Callback c = requestTypeTotCallback.get(m.getClass()) ;
    	if (c == null) 
    	{
    	    c = broadcastTypeTotCallback.get(m.getClass()) ;
    		return c;
    	}
    	return c;
   }
    
   

}