package cs455.nfs.messagehandlers;

import java.io.File;
import java.io.IOException;

import cs455.nfs.connection.DataTransfer;
import cs455.nfs.directorystructure.Entity;
import cs455.nfs.remote.DirectoryService;
import cs455.nfs.remote.Setting;
import cs455.nfs.wireformates.Message;
import cs455.nfs.wireformates.MessageType;
import cs455.nfs.wireformates.RelocationRequest;
import cs455.nfs.wireformates.RelocationResponse;
import cs455.nfs.wireformates.Response;
import cs455.nfs.wireformates.StoreFileRequest;


public class RelocationRequestHandler  implements Handler{
	
	private RelocationResponse getResponseMSG(byte status, String info){
		RelocationResponse responseMSG = new RelocationResponse();
		responseMSG.setStatusCode(status);
		responseMSG.setAdditionalInfo(info);
		return responseMSG;
	}
	
	private RelocationResponse localTransfer(File file, String dstDIr){
		File root 	= new File(Setting.ROOT_DIRECTORY);		
		File dst 	= Entity.findPath(root,dstDIr);
		if (dst==null)
			return getResponseMSG(MessageType.FALSE,dstDIr+": No such directory on the Server "+DirectoryService.HOST_NAME+" has been found!");
		
		if (file.renameTo(new File(dst, file.getName())))
			return getResponseMSG(MessageType.TRUE,"The operation is successfully executed!");
		
		return getResponseMSG(MessageType.FALSE,"The operation could not be executed!");
	}
	
	private Message remoteTransfer(File file, String dstDIr, String hostIP, int port){
		StoreFileRequest msg = new StoreFileRequest();
		msg.setDstDir(dstDIr);
		msg.setFile(file);
		Response responseMSG = null;
		try {
			responseMSG = (Response) DataTransfer.sendFileMSG(msg, hostIP, port);
			if (responseMSG == null)
				return getResponseMSG(MessageType.FALSE,"The file could not be found on the first server("+DirectoryService.HOST_NAME+")");
			
			if (responseMSG.getStatusCode() == MessageType.TRUE){
				file.delete();
				responseMSG.setAdditionalInfo("The vmv operation has been successfuly executed");
			}
			return 	responseMSG;	
			
		} catch (IOException e) {
			return getResponseMSG(MessageType.FALSE,e.getMessage());
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
		}
		return getResponseMSG(MessageType.FALSE,"The operation could not be executed!");
	}
	
	@Override
	public Message handle(Message msg) {
		String srcPath	= ((RelocationRequest) msg).getSrcDir();
		String dstPath	= ((RelocationRequest) msg).getDstDir();
		File root 		= new File(Setting.ROOT_DIRECTORY);
		File fileEntity = Entity.findPath(root,srcPath);
		if (fileEntity == null)
			return getResponseMSG(MessageType.FALSE,srcPath+": No such file on the Server  "+DirectoryService.HOST_NAME+" has been found!");
		
		String dstHost	= ((RelocationRequest) msg).getDstHostIP();
		if(dstHost.compareTo(DirectoryService.HOST_NAME)==0)
			return localTransfer(fileEntity, dstPath);
		
		return remoteTransfer(fileEntity, dstPath,dstHost, ((RelocationRequest) msg).getDstPort());
	}

}
