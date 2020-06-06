package cs455.nfs.wireformates;

import cs455.nfs.util.ByteStream;

//it is a supper class of RegisterResponse and DegisterResponse 
public class Response extends Message{

	private byte statusCode;
	private String additionalInfo;
	
	public Response(int MessageID) {
		super(MessageID);
	}

	@Override
	public void initiate(byte[] byteStream) {
		currentIndex = 4;
		setStatusCode(unpackByteField(byteStream));
		setAdditionalInfo(unpackStringField(byteStream));
	}

	@Override
	public byte[] packMessageBody(){
		byte[] StatusCodeBytes = {getStatusCode()};
		return ByteStream.join( StatusCodeBytes , ByteStream.packString(getAdditionalInfo()) );
	}

	public byte getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(byte statusCode) {
		this.statusCode = statusCode;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
}
