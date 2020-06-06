package cs455.nfs.messagehandlers;

import cs455.nfs.directorystructure.Entity;
import cs455.nfs.remote.Setting;
import cs455.nfs.wireformates.Message;
import cs455.nfs.wireformates.PeekResponse;

public class PeekRequestHandler extends RetrieveDirectoryHandler{
	@Override
	public Message handle(Message msg) {
		PeekResponse peekResponse = new PeekResponse();
		Entity root = getDS(Setting.ROOT_DIRECTORY);
		if (root!=null)
			peekResponse.setRoot(root);
		return peekResponse;
	}

}
