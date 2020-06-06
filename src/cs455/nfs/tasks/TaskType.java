package cs455.nfs.tasks;

public abstract class TaskType {
	public abstract TaskType 			nextTask();
	public abstract	int 				getOperationKey();
	public abstract boolean				execute(Task task);
}
