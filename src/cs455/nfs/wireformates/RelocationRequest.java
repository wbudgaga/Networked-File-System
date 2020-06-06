package cs455.nfs.wireformates;

import cs455.nfs.util.ByteStream;

public class RelocationRequest extends Message{
	private String 	fileName;
	private String 	srcDir;
	private String 	dstDir;
	private String 	dstHostIP;
	private int 	dstPort;

	public RelocationRequest() {
		super(RELOCATION_REQUEST);
	}
	
	@Override
	public void initiate(byte[] byteStream) {
		currentIndex = 4;
		setFileName	( unpackStringField( byteStream ) );
		setSrcDir  	( unpackStringField( byteStream ) );
		setDstDir  	( unpackStringField( byteStream ) );
		setDstHostIP( unpackStringField( byteStream ) );
		setDstPort	( unpackIntField   ( byteStream ) );
	}

	@Override
	protected byte[] packMessageBody() {
		byte[] messageBody 	= ByteStream.join( ByteStream.packString(getFileName()) , ByteStream.packString(getSrcDir())		);
		messageBody			= ByteStream.join( messageBody 							, ByteStream.packString(getDstDir())		);
		messageBody			= ByteStream.join( messageBody 							, ByteStream.packString(getDstHostIP())		);
		return 				  ByteStream.join( messageBody 							, ByteStream.intToByteArray(getDstPort())	);
	}

	/*==========================================================
	 * 		The following methods are set & get methods
	 ==========================================================*/

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}

	public String getDstDir() {
		return dstDir;
	}

	public void setDstDir(String dstDir) {
		this.dstDir = dstDir;
	}

	public String getDstHostIP() {
		return dstHostIP;
	}

	public void setDstHostIP(String dstHostIP) {
		this.dstHostIP = dstHostIP;
	}

	public int getDstPort() {
		return dstPort;
	}

	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}
	
	public static void main(String args[]){
		Message msg = new RelocationRequest();
		((RelocationRequest) msg).setFileName("test.java");
		((RelocationRequest) msg).setSrcDir("c:/tmp/myWork");
		((RelocationRequest) msg).setDstDir("d:/dst/result");
		((RelocationRequest) msg).setDstHostIP("Faure");
		((RelocationRequest) msg).setDstPort(8001);
		byte[] p = msg.packMessage();
		RelocationRequest msg1 = new RelocationRequest();
		msg1.initiate(p);
		
		System.out.println("message nr: "+ msg1.getMessageID());
		System.out.println("getFileName: "+ msg1.getFileName());
		System.out.println("getSrcDir: "+ msg1.getSrcDir());
		System.out.println("getDstDir: "+ msg1.getDstDir());
		System.out.println("getDstHostIP: "+ msg1.getDstHostIP());
		System.out.println("getDstHostIP: "+ msg1.getDstPort());
	}
}

