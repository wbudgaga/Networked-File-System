package cs455.nfs.messagehandlers;

import cs455.nfs.directorystructure.Entity;
import cs455.nfs.remote.Setting;
import cs455.nfs.wireformates.Message;
import cs455.nfs.wireformates.MountRequest;
import cs455.nfs.wireformates.MountResponse;

public class MountRequestHandler extends RetrieveDirectoryHandler{

	@Override
	public Message handle(Message msg) {
		Entity root;
		MountResponse 	mountResponse	= new MountResponse();
		String 			requiredPath 	= ((MountRequest) msg).getDirPath();
		root 							= getDS(Setting.ROOT_DIRECTORY +"/"+ requiredPath);
		if (root != null)
			mountResponse.setRoot(root);
		return mountResponse;
	}
}
