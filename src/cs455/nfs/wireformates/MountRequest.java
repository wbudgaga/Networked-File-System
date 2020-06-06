package cs455.nfs.wireformates;

import cs455.nfs.util.ByteStream;

public class MountRequest extends Message{
	private String dirPath;
	public MountRequest() {
		super(MOUNT_REQUEST);
	}
	
	public MountRequest(String dirPath) {
		super(MOUNT_REQUEST);
		this.setDirPath(dirPath);
	}

	@Override
	public void initiate(byte[] byteStream) {
		currentIndex = 4;
		dirPath =  unpackStringField(byteStream);
	}

	@Override
	protected byte[] packMessageBody() {
		return ByteStream.packString(dirPath);
	}

	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}
}
