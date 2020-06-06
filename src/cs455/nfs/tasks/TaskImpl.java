package cs455.nfs.tasks;

import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;

import cs455.nfs.messagehandlers.MessageHandler;

public class TaskImpl implements Task{
	private 		TaskType 			task;
	protected 		ByteBuffer  		buffer;
	protected 		SelectionKey		key;
	protected	 	MessageHandler		messageHandler;
	
	public TaskImpl(MessageHandler mHandler){
		messageHandler 	= mHandler;
		task 			= new ReadTask();
//		buffer 			= ByteBuffer.allocate(Setting.BUFFERSIZE);
	}
	
	@Override
	public synchronized void execute() {
		if(task == null || !task.execute(this)){		// execute current task & check the return value
			closeChannel();				// cancel the key
			return;
		}
		
		if (buffer!=null){
			task = task.nextTask();		// turn into new task
			changeInterestOps();		// change the interest operation
		}
	}
	
	// Register the channel of this task with its operation key, and attach this task with its key
	private void changeInterestOps() {
		try{
			key.interestOps(task.getOperationKey());
		}catch(CancelledKeyException e){
			closeChannel();
		}
	}

	private void closeChannel() {
		key.cancel();
		task = null;
	}
	@Override
	public void  setKey(SelectionKey key){
		this.key = key;
	}
}
