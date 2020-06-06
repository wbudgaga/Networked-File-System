package cs455.nfs.messagehandlers;

import cs455.nfs.wireformates.Message;

public interface Handler {
	public Message handle(Message msg);
}
