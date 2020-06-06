package cs455.nfs.tasks;

import java.nio.channels.SelectionKey;

public interface Task {
	public void execute(); 
	public void setKey(SelectionKey key);
}
