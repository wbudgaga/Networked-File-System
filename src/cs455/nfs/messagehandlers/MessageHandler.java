package cs455.nfs.messagehandlers;
			       
import java.util.HashMap;
import java.util.Map;

import cs455.nfs.wireformates.MessageType;

public class MessageHandler {
	private static MessageHandler instance;
	private Map<Integer,Class<Handler>> classList 	= new HashMap<Integer,Class<Handler>>(); 
	
	private void loadMessageHandlerClasses() throws ClassNotFoundException{
		MessageType.HandlersClassName[] classIDs 	= MessageType.HandlersClassName.values();
		for (int i=0;i<classIDs.length;++i){
			@SuppressWarnings("unchecked")
			Class<Handler> messageHandlerClass 		= (Class<Handler>) Class.forName("cs455.nfs.messagehandlers."+ classIDs[i].toString());
			classList.put(new Integer(i), messageHandlerClass);
		}
	}

	private MessageHandler() throws ClassNotFoundException{	
		loadMessageHandlerClasses();
	}

	public  static  MessageHandler getInstance() throws ClassNotFoundException{
		if (instance == null)
			instance = new MessageHandler();
	    return instance;
	}
	
	public Handler getHandle(int messageID) throws InstantiationException, IllegalAccessException{
		Class<Handler> messageClass = classList.get(new Integer(messageID));
		Handler messageHandler 		= (Handler) messageClass.newInstance();
		return messageHandler;
	}
}
