package cs455.nfs.wireformates;

public class PeekRequest extends Message{

	public PeekRequest() {
		super(PEEK_REQUEST);
	}

	@Override
	public void initiate(byte[] byteStream) {
	}

	@Override
	protected byte[] packMessageBody() {
		return null;
	}
}
