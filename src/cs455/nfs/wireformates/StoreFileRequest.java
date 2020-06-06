package cs455.nfs.wireformates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cs455.nfs.directorystructure.Entity;
import cs455.nfs.remote.Setting;
import cs455.nfs.util.ByteStream;

public class StoreFileRequest extends Message{
	private String 	fileName;
	private String 	dstDir;
	private File 	file;
	private int 	lastChunkStatus;
	
	public StoreFileRequest() {
		super(STORE_FILE_REQUEST);
		file= null;
		setLastChunkStatus(-1);
		
	}

	private boolean createFile(File dstDirEntity){
		File filesLocation	=	Entity.findPath(dstDirEntity , dstDir); 		
		File tmpFile		=	new File(filesLocation,fileName);
    	if(tmpFile.exists())
    		tmpFile.delete();
		try {
			return tmpFile.createNewFile();
		} catch (IOException e) {}
    	return false;	
	}
	
	private void handleFile(byte[] byteStream){
		int chunkNumber	= unpackIntField   ( byteStream );
		File dstDir		= new File(Setting.ROOT_DIRECTORY);
		if (chunkNumber == 0){
			if(!createFile(dstDir))
				return;
		}
			
		File tmpPathEntity 	= Entity.findPath(dstDir,fileName);
		if (tmpPathEntity == null)
			return;
		
		byte[] buffer = readObjectBytes(byteStream);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(tmpPathEntity,true);
			fileOutputStream.write(buffer);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (isLastChunk())
			setFile(tmpPathEntity);
	}
	
	@Override
	public void initiate(byte[] byteStream) {
		currentIndex = 4;
		setFileName			( unpackStringField( byteStream ) );
		setDstDir  			( unpackStringField( byteStream ) );
		setLastChunkStatus 	( unpackIntField   ( byteStream ) );
		if (byteStream.length > currentIndex)
			handleFile(byteStream);
	}

	@Override
	protected byte[] packMessageBody() {
		byte[] messageBody = ByteStream.join( ByteStream.packString(getFileName()), ByteStream.packString(getDstDir()) );
		return ByteStream.join( messageBody , ByteStream.intToByteArray(getLastChunkStatus()) );
	}

	public String getDstDir() {
		return dstDir;
	}

	public void setDstDir(String srcDir) {
		this.dstDir = srcDir;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		setFileName(file.getName());
		this.file = file;
	}

	public boolean isLastChunk() {
		return lastChunkStatus == MessageType.TRUE;
	}

	public int getLastChunkStatus() {
		return this.lastChunkStatus;
	}

	public void setLastChunkStatus(int status) {
		this.lastChunkStatus = status;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
