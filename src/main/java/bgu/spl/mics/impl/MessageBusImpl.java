package bgu.spl.mics.impl;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Request;
import bgu.spl.mics.RequestCompleted;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageBusImpl implements MessageBus {

	private static final Logger logger = Logger.getGlobal();
	protected Map<Class<? extends Request>,CopyOnWriteArrayList<MicroService>> requestTypeMap;
	protected Map<Class<? extends Broadcast>,CopyOnWriteArrayList<MicroService>> broadcastTypeMap;
	protected Map<Class<? extends Request>,Integer> lastWorkedMap;
	protected Map<Request,MicroService> requestToRequesterMap;
	protected Map<Request,MicroService> requestToHandlerMap; 
	protected Map<MicroService,ConcurrentLinkedQueue<Message>> miscroServiceToqueueMap;
	protected Map<MicroService,CopyOnWriteArrayList<Class<? extends Request>>> miscroServiceToRequestsTypeMap;
	protected Map<MicroService,CopyOnWriteArrayList<Class<? extends Broadcast>>> miscroServiceToBroadcastsTypeMap;


	protected static class MessageBusImplHolder {//thread safe singelton
		protected static MessageBusImpl messageBus = new MessageBusImpl();
	}

	protected MessageBusImpl(){//MessageBus constructor
		requestTypeMap=new HashMap<Class<? extends Request>,CopyOnWriteArrayList<MicroService>>();
		broadcastTypeMap=new HashMap<Class<? extends Broadcast>,CopyOnWriteArrayList<MicroService>>();
		lastWorkedMap=new HashMap<Class<? extends Request>,Integer>();
		requestToRequesterMap=new HashMap<Request,MicroService>();
		requestToHandlerMap=new HashMap<Request,MicroService>();
		miscroServiceToqueueMap = new HashMap<MicroService,ConcurrentLinkedQueue<Message>>();
		miscroServiceToRequestsTypeMap = new HashMap<MicroService,CopyOnWriteArrayList<Class<? extends Request>>>();
		miscroServiceToBroadcastsTypeMap = new HashMap<MicroService,CopyOnWriteArrayList<Class<? extends Broadcast>>>();
	}


	public static MessageBusImpl getMessageBus() {
		return MessageBusImplHolder.messageBus;
	}

	public synchronized void register(MicroService m){

		ConcurrentLinkedQueue<Message> mQueue = new ConcurrentLinkedQueue<Message>();
		miscroServiceToqueueMap.put(m, mQueue);	
	}

	public void unregister(MicroService m){
		if (miscroServiceToqueueMap.get(m) !=null){//meaning m is Registered and have a queue.
			CopyOnWriteArrayList<Class<? extends Request>> requestsList = miscroServiceToRequestsTypeMap.get(m);

			Class typeOfMessage;
			if (requestsList != null){
				for (int i=0; i<requestsList.size();i++)
				{
					typeOfMessage = requestsList.get(i);
					CopyOnWriteArrayList<MicroService> currArrayList=requestTypeMap.get(typeOfMessage);
					int indexToRemove = currArrayList.indexOf(m);
					currArrayList.remove(indexToRemove);
					int currArrayListSize = currArrayList.size();//size after removal.
					int lastWorkedIndex = lastWorkedMap.get(typeOfMessage);//index, in the currArrayList, of the last micro-service how worked on m request type.
					if (indexToRemove<=lastWorkedIndex){
						switch (lastWorkedIndex){
						case -1://no micro-service had worked on this type of request yet.
						case 0:
							lastWorkedIndex=currArrayListSize-1;
						default:
							lastWorkedIndex--;
						}
						lastWorkedMap.put(typeOfMessage, lastWorkedIndex);
					}
				}
			}

			CopyOnWriteArrayList<Class<? extends Broadcast>> broadcastsList = miscroServiceToBroadcastsTypeMap.get(m);
			if (broadcastsList != null){

				for (int i=0; i<broadcastsList.size();i++)
				{
					typeOfMessage = broadcastsList.get(i);
					CopyOnWriteArrayList<MicroService> currArrayList=broadcastTypeMap.get(typeOfMessage);
					currArrayList.remove(m);
				}
			}
			miscroServiceToqueueMap.remove(m);
		}
		else{
			return;
		}

	}

	public synchronized Message awaitMessage(MicroService m) throws InterruptedException{
		if (miscroServiceToqueueMap.get(m) !=null){//meaning m is Registered and have a queue.
			while (miscroServiceToqueueMap.get(m).isEmpty())
			{
				try {
					this.wait();
				} catch (InterruptedException e) {}
			}		
			return miscroServiceToqueueMap.get(m).poll();
		}
		throw new IllegalStateException("MicroService m isn't registered");
	}

	public synchronized void subscribeRequest(Class<? extends Request> type, MicroService m){
		if (miscroServiceToqueueMap.get(m) !=null){//meaning m is Registered and have a queue.
			CopyOnWriteArrayList<MicroService> currArrayList1;
			if (requestTypeMap.containsKey(type)){
				currArrayList1=requestTypeMap.get(type);
				currArrayList1.add(m);
			}
			else{
				currArrayList1 = new CopyOnWriteArrayList<MicroService>();
				requestTypeMap.put(type, currArrayList1);
				lastWorkedMap.put(type, -1);
				currArrayList1.add(m);
			}
		}
		CopyOnWriteArrayList<Class<? extends Request>> currArrayList2;
		if (miscroServiceToRequestsTypeMap.get(m) == null){
			currArrayList2 = new CopyOnWriteArrayList<Class<? extends Request>>();
			miscroServiceToRequestsTypeMap.put(m, currArrayList2);
			currArrayList2.add(type);
		}
		else{
			currArrayList2 = miscroServiceToRequestsTypeMap.get(m);
			currArrayList2.add(type);
		}



	}

	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m){
		if (miscroServiceToqueueMap.get(m) !=null){//meaning m is Registered and have a queue.
			CopyOnWriteArrayList<MicroService> currArrayList1;
			if (broadcastTypeMap.containsKey(type)){
				currArrayList1=broadcastTypeMap.get(type);
				currArrayList1.add(m);
			}
			else{
				currArrayList1 = new CopyOnWriteArrayList<MicroService>();
				broadcastTypeMap.put(type, currArrayList1);
				currArrayList1.add(m);
			}		
			CopyOnWriteArrayList<Class<? extends Broadcast>> currArrayList2;
			if (miscroServiceToBroadcastsTypeMap.get(m) == null){
				currArrayList2 = new CopyOnWriteArrayList<Class<? extends Broadcast>>();
				miscroServiceToBroadcastsTypeMap.put(m, currArrayList2);
				currArrayList2.add(type);
			}
			else{
				currArrayList2=miscroServiceToBroadcastsTypeMap.get(m);
				currArrayList2.add(type);
			}

		}
	}
	public synchronized void sendBroadcast(Broadcast b){

		Class typeOfMessage=b.getClass();
		CopyOnWriteArrayList<MicroService> currArrayList=broadcastTypeMap.get(typeOfMessage);
		if (currArrayList != null){//no micro-services had been subscribed to this broadcast yet.
			int currBroadcastArrayListSize = currArrayList.size();
			if(currBroadcastArrayListSize>0){//no micro-services are currently subscribed to this broadcast.
				MicroService currMicroService;
				for (int j=0; j<currBroadcastArrayListSize;j++){
					currMicroService=currArrayList.get(j);
					ConcurrentLinkedQueue<Message> currQueue = miscroServiceToqueueMap.get(currMicroService); 
					currQueue.add(b);
				}
				this.notifyAll();
			}	
		}
	}

	public synchronized boolean sendRequest(Request<?> r, MicroService requester){
		requestToRequesterMap.put(r, requester);
		Class typeOfMessage=r.getClass();
		CopyOnWriteArrayList<MicroService> currArrayList=requestTypeMap.get(typeOfMessage);
		if (currArrayList != null){//no micro-services had been subscribed to this request yet.
			int currArrayListSize = currArrayList.size();
			if(currArrayListSize>0){//no micro-services are currently subscribed to this request.
				int j = lastWorkedMap.get(typeOfMessage);
				j++;
				j=j % currArrayListSize;
				MicroService handlerMicroSarvice = currArrayList.get(j);
				ConcurrentLinkedQueue<Message> currQueue = miscroServiceToqueueMap.get(handlerMicroSarvice);
				currQueue.add(r);
				lastWorkedMap.put(typeOfMessage,j);
				requestToHandlerMap.put(r, handlerMicroSarvice);
				this.notifyAll();
				return true;		
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	public synchronized <T> void complete(Request<T> r, T result){
		RequestCompleted<T> completeMessage = new RequestCompleted<T>(r, result);
		MicroService requester=requestToRequesterMap.get(r);
		ConcurrentLinkedQueue<Message> currQueue = miscroServiceToqueueMap.get(requester);
		currQueue.add(completeMessage);
		logger.log(Level.INFO, "Request " + r.getClass().getSimpleName() + " was completed and " + requester.getName() + " was notifyed");
		this.notifyAll();
	}

}