package cs455.nfs.wireformates;

import cs455.nfs.util.ByteStream;

public class MkdirRequest extends Message{
	private String 	dir;
	private String 	dstDir;

	public MkdirRequest() {
		super(MKDIR_REQUEST);
	}

	@Override
	public void initiate(byte[] byteStream) {
		currentIndex = 4;
		setDir			( unpackStringField( byteStream ) );
		setDstDir  		( unpackStringField( byteStream ) );
	}

	@Override
	protected byte[] packMessageBody() {
		return ByteStream.join( ByteStream.packString(getDir()), ByteStream.packString(getDstDir()) );
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getDstDir() {
		return dstDir;
	}

	public void setDstDir(String dstDir) {
		this.dstDir = dstDir;
	}

}
