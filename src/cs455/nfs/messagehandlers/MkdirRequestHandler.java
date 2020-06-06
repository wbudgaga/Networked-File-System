package cs455.nfs.messagehandlers;

import java.io.File;

import cs455.nfs.directorystructure.Entity;
import cs455.nfs.remote.DirectoryService;
import cs455.nfs.remote.Setting;
import cs455.nfs.wireformates.Message;
import cs455.nfs.wireformates.MessageType;
import cs455.nfs.wireformates.MkdirRequest;
import cs455.nfs.wireformates.RelocationResponse;

public class MkdirRequestHandler implements Handler{
	private RelocationResponse getResponseMSG(byte status, String info){
		RelocationResponse responseMSG = new RelocationResponse();
		responseMSG.setStatusCode(status);
		responseMSG.setAdditionalInfo(info);
		return responseMSG;
	}

	@Override
	public Message handle(Message msg) {
		String dstDir	= ((MkdirRequest) msg).getDstDir();
		File root 		= new File(Setting.ROOT_DIRECTORY);
		File dirEntity 	= Entity.findPath(root,dstDir);
		if (dirEntity == null)
			return getResponseMSG(MessageType.FALSE,dstDir+": No such dirctory on the Server  "+DirectoryService.HOST_NAME+" has been found!");
		String dir		= ((MkdirRequest) msg).getDir();
		File newDir = new File(dirEntity.getPath(),dir);
		
		if (newDir.mkdir())
			return getResponseMSG(MessageType.TRUE,"The operation is successfully executed!");
		
		return getResponseMSG(MessageType.FALSE,"The operation could not be executed on the server!");
	}
}
