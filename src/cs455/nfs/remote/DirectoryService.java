package cs455.nfs.remote;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

import cs455.nfs.messagehandlers.MessageHandler;
import cs455.nfs.tasks.AcceptTask;
import cs455.nfs.tasks.Task;
import cs455.nfs.threadpool.ThreadPoolManager;

public class DirectoryService {
	
	private final Selector 				selector;
	private final ServerSocketChannel 	serverChannel;
	private 	  ThreadPoolManager 	threadPool;
	public  	  static int			PORT_NUMBER ;
	public  	  static String			HOST_NAME ;
	
	public DirectoryService(int port) throws IOException {
		PORT_NUMBER = port;
		selector = Selector.open();
	 
	    serverChannel = ServerSocketChannel.open();
	    initServerChannel(port);    
	}
	
	private void initServerChannel(int port) throws IOException{
		// Set the server channel in non-blocking mode 
		serverChannel.configureBlocking(false);
		InetSocketAddress address = new InetSocketAddress(port);
	    // Bind the server socket to the specified port on the local machine
		serverChannel.socket().bind(address);
	}

	private void start() throws IOException, ClassNotFoundException{
		MessageHandler mHandler = MessageHandler.getInstance();
		// create and attach accept task with the selection key of server channel 
		Task atask = new AcceptTask(mHandler);
		SelectionKey acceptSelectionKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT, atask);
		atask.setKey(acceptSelectionKey);
		HOST_NAME = InetAddress.getLocalHost().getHostName();
		System.out.println("Server  running on  " + HOST_NAME);
		
		while(true){
			if (selector.select(100)==0)  continue;
			
			Set<SelectionKey> selectedKeys 			= selector.selectedKeys();
			
			Iterator<SelectionKey> selectedKeysIter = selectedKeys.iterator();
			while (selectedKeysIter.hasNext()){ 
				SelectionKey sKey 	= (SelectionKey)selectedKeysIter.next();
				Task task 			= (Task) sKey.attachment();
				threadPool.addTask(task);
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			selectedKeys.clear();
		}
	}

	private boolean createRootDirectory(){
		try{
			// Creating root directory
			File dir = new File(Setting.ROOT_DIRECTORY);
			if (dir.exists())
				return true;
			
			return dir.mkdirs(); 	  
		}catch (Exception e){
			return false;
		}	  
	}

	protected void startup(int threadPoolSize) throws IOException, ClassNotFoundException{
		if(!createRootDirectory()){
			System.err.println("Root Directory  "+ Setting.ROOT_DIRECTORY + " could not be created");
			return;
		}
			
		threadPool = new ThreadPoolManager(threadPoolSize);
		threadPool.start();

		start();
	}

	public static void main(String args[]){
		if (args.length < 1) {
			System.err.println("Server:  Usage:");
			System.err.println("       java cs455.nfs.remote.DirectoryService portnum");
		    return;
		}
		
		int port = Integer.parseInt(args[0]);		
		
		try {
			DirectoryService server = new DirectoryService(port);
			server.startup(Setting.THREADPOOL_SIZE);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
	}

}
