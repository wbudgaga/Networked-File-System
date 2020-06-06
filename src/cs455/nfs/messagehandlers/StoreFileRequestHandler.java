package cs455.nfs.messagehandlers;

import java.io.File;

import cs455.nfs.directorystructure.Entity;
import cs455.nfs.remote.DirectoryService;
import cs455.nfs.remote.Setting;
import cs455.nfs.wireformates.Message;
import cs455.nfs.wireformates.MessageType;
import cs455.nfs.wireformates.StoreFileRequest;
import cs455.nfs.wireformates.StoreFileResponse;

public class StoreFileRequestHandler implements Handler{
	
	private StoreFileResponse getResponseMSG(byte status, String info){
		StoreFileResponse responseMSG = new StoreFileResponse();
		responseMSG.setStatusCode(status);
		responseMSG.setAdditionalInfo(info);
		return responseMSG;
	}

	@Override
	public Message handle(Message msg) {
		int status	= ((StoreFileRequest) msg).getLastChunkStatus();	

		if (status == -1){
			String 	dstDIr			= ((StoreFileRequest) msg).getDstDir();
			File 	root 			= new File(Setting.ROOT_DIRECTORY);		
			File 	dstPathEntity 	= Entity.findPath(root,dstDIr);
			if (dstPathEntity 	== null)
				return getResponseMSG(MessageType.FALSE,dstDIr+": No such directory on the Server "+DirectoryService.HOST_NAME+" has been found!");

			return getResponseMSG(MessageType.TRUE,"");
		}else if(status == MessageType.TRUE)
				return getResponseMSG(MessageType.TRUE,"");

		return null;		
	}

}
