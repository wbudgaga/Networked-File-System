package cs455.nfs.tasks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteTask extends TaskType{
	
	@Override
	public boolean execute(Task task){
		try {
			ByteBuffer outputBuffer	=  ((TaskImpl) task).buffer;
			SocketChannel channel  	= (SocketChannel) ((TaskImpl) task).key.channel();
			// Make sure that the buffer is fully drained
			while (outputBuffer.hasRemaining( )) {
				channel.write (outputBuffer);
			}			
			outputBuffer.clear();
		} catch (ClosedChannelException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public TaskType nextTask() {
		return new ReadTask();
	}

	@Override
	public int getOperationKey() {
		return SelectionKey.OP_WRITE;
	}
}
