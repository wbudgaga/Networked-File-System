package cs455.nfs.wireformates;
// contains the messages types and their equivalent classes' names
// it is very important to keep the order the same in both lists 
public interface MessageType {
	
	public static final int PEEK_REQUEST 		= 0;
	public static final int PEEK_RESPONSE 		= 1;
	public static final int MOUNT_REQUEST 		= 2;
	public static final int MOUNT_RESPONSE 		= 3;	
	public static final int RELOCATION_REQUEST 	= 4;
	public static final int RELOCATION_RESPONSE = 5;
	public static final int STORE_FILE_REQUEST 	= 6;
	public static final int STORE_FILE_RESPONSE = 7;
	public static final int MKDIR_REQUEST 		= 8;
	public static final int MKDIR_RESPONSE 		= 9;
	
	public static final byte TRUE					= 10;
	public static final byte FALSE					= 11;
			
	public static enum MessagesClassName{
		PeekRequest,
		PeekResponse,
		MountRequest,
		MountResponse,
		RelocationRequest,
		RelocationResponse,
		StoreFileRequest,
		StoreFileResponse,
		MkdirRequest,
		MkdirResponse;
		
		 public static String get(int i){
			 return values()[i].toString();
		 }
	}

	public static enum HandlersClassName{
		PeekRequestHandler,
		PeekResponseHandler,
		MountRequestHandler,
		MountResponseHandler,
		RelocationRequestHandler,
		RelocationResponseHandler,
		StoreFileRequestHandler,
		StoreFileResponseHandler,
		MkdirRequestHandler,
		MkdirResponseHandler;

		 public static String get(int i){
			 return values()[i].toString();
		 }
	}

	
}
