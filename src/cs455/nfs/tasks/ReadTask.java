package cs455.nfs.tasks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import cs455.nfs.messagehandlers.Handler;
import cs455.nfs.messagehandlers.MessageHandler;
import cs455.nfs.util.ByteStream;
import cs455.nfs.wireformates.Message;
import cs455.nfs.wireformates.MessageFactory;

public class ReadTask  extends TaskType{

	private Message handle(byte[] byteStream, MessageHandler mhandler) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		int messageID 	= Message.unpackMessageID(byteStream);
		Message msg 	= MessageFactory.getInstance().createMessage(byteStream);
		Handler handler = mhandler.getHandle(messageID);
		return handler.handle(msg);
	}
	
	private byte[] readBytes(SocketChannel channel, int length) throws IOException{
		ByteBuffer inputBuffer = ByteBuffer.allocate(length);
		
		int totalBytesRcvd 		= 0;  // Total bytes received so far
		int bytesRcvd;           // Bytes received in last read
		while (totalBytesRcvd < length) {
			bytesRcvd = channel.read(inputBuffer);
			if (bytesRcvd == -1)
			  	return null;	
			totalBytesRcvd += bytesRcvd;
		}
		return inputBuffer.array();
	}

	@Override
	// There is pending data on socket channel ready to read
	public boolean execute(Task task) {
		try {
			byte[] bytes 	= readBytes((SocketChannel) ((TaskImpl) task).key.channel(),4);
			if (bytes == null)
				return false;
			
			int bodyLength 		= ByteStream.byteArrayToInt(bytes);
			bytes 				= readBytes((SocketChannel) ((TaskImpl) task).key.channel(),bodyLength);
			if (bytes == null)
			  	return false;	
			
			((TaskImpl) task).buffer=null;
			Message responseMSG 	= handle(bytes, ((TaskImpl) task).messageHandler);
			if (responseMSG != null){
				byte [] byteStream 			= ByteStream.addPacketHeader(responseMSG.packMessage());
				((TaskImpl) task).buffer 	=  ByteBuffer.wrap(byteStream);
			}
			return true;
			
		} catch (IOException e) {
			return false;
		} catch (InstantiationException e) {
			System.err.println("One of message's class or handler's class could not be initiated. Please check the packages messagehandlers and wireformates");
			return false;
		} catch (IllegalAccessException e) {
			System.err.println("Instance of one of message's class or handler's class could not be created. Please check the packages messagehandlers and wireformates");
			return false;
		} catch (ClassNotFoundException e) {
			System.err.println("One of message's class or handler's class could not be found. Please check the packages messagehandlers and wireformates");		
			return false;
		}
	}

	@Override
	public TaskType nextTask() {
		// Return the new write task in order to send a response on the same channel.
		return new WriteTask();
	}
	
	@Override
	public int getOperationKey() {
		return  SelectionKey.OP_READ;
	}
}
