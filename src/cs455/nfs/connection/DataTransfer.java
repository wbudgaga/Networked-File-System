package cs455.nfs.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import cs455.nfs.remote.DirectoryService;
import cs455.nfs.remote.Setting;
import cs455.nfs.util.ByteStream;
import cs455.nfs.wireformates.Message;
import cs455.nfs.wireformates.MessageFactory;
import cs455.nfs.wireformates.MessageType;
import cs455.nfs.wireformates.Response;
import cs455.nfs.wireformates.StoreFileRequest;

public class DataTransfer {
	public static Socket connect(String serverHost, int port){
		try {
			return  new Socket(serverHost, port);
		} catch (UnknownHostException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	private static void sendData(OutputStream outStream, byte[] bytes) throws IOException{
		outStream.write(bytes,0, bytes.length);
		outStream.flush();
	}
	
	public static void sendFileData(OutputStream outStream, FileInputStream fileOutputStream, StoreFileRequest msg) throws IOException{
		int 	length 			= (int) msg.getFile().length();
		boolean keepRunning 	= true;
	
		for (int i = 0; keepRunning; ++i){
			int remainingSize = (int) (length - i * Setting.BUFFERSIZE);
			int len;
			if (remainingSize < Setting.BUFFERSIZE){
				len 		= remainingSize;
				keepRunning = false;
				msg.setLastChunkStatus(MessageType.TRUE);
			}else{
				len 		= Setting.BUFFERSIZE;
				msg.setLastChunkStatus(MessageType.FALSE);
			}
			byte[] bytes		= ByteStream.readFileBytes(fileOutputStream, len);
			byte[] messageBody 	= ByteStream.join(ByteStream.intToByteArray(i), ByteStream.addPacketHeader(bytes));
			messageBody 		= ByteStream.join(msg.packMessage(),messageBody);
			sendData(outStream,  ByteStream.addPacketHeader(messageBody));
		}
	}
	
	public static Message sendFileMSG(Message msg,String ip, int port) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Socket	socket = connect(ip, port);
		if (socket== null)
			throw new IOException(DirectoryService.HOST_NAME+": Connection with "+ ip +" couldn't be established!");
	
		File file = ((StoreFileRequest)msg).getFile(); 
		if (!file.exists())
			return null;
		
		sendData(socket.getOutputStream(),  ByteStream.addPacketHeader(msg.packMessage()));
		Response responseMessage = (Response) readResponseMessage(socket.getInputStream());
		if ( responseMessage.getStatusCode() == MessageType.TRUE){
			sendFileData(socket.getOutputStream(), new FileInputStream(file),(StoreFileRequest) msg);
			responseMessage = (Response)  readResponseMessage(socket.getInputStream());
		}
		socket.close();
		return responseMessage; 
	}

	
	public static Message sendMessage(Message msg,String ip, int port) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Socket	socket = connect(ip, port);
		if (socket== null)
			throw new IOException(DirectoryService.HOST_NAME+": Connection with "+ ip +" couldn't be established!");
		
		socket.setKeepAlive(true);
		sendData( socket.getOutputStream() , ByteStream.addPacketHeader(msg.packMessage()));		
		Message responseMessage = readResponseMessage(socket.getInputStream());
		socket.close();
		return responseMessage; 
	}
	
	public static byte[] readBytes(InputStream inStream,int length) throws SocketException, IOException{
		int totalBytesRcvd 		= 0;  // Total bytes received so far
		byte[] byteBuffer		= new byte[length];
		int bytesRcvd;           // Bytes received in last read
		while (totalBytesRcvd < length) {
		      if ((bytesRcvd = inStream.read(byteBuffer, totalBytesRcvd, length - totalBytesRcvd)) == -1)
		    	  throw new SocketException("Connection close prematurely");
		      
		      totalBytesRcvd += bytesRcvd;
		}
	    return byteBuffer;
	}
		
	 public static Message readResponseMessage(InputStream inStream) throws SocketException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		 byte[] bytes 	= readBytes(inStream,4); 
		 int bodyLength	= ByteStream.byteArrayToInt(bytes);
		 bytes 			= readBytes(inStream,bodyLength);
		 return MessageFactory.getInstance().createMessage(bytes);
	}
}
