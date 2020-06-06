package cs455.nfs.directorystructure;

import cs455.nfs.util.ByteStream;

public class VFile extends Entity{

	public VFile(String name) {
		super(name);
		entityCode = FILE;
	}

	@Override
	public void add(Entity obj) {
	}

	@Override
	public void remove(Entity obj) {		
	}

	@Override
	public void ls() {
		listContent();
	}

	@Override
	public Entity cd() {
		return null;
	}

	@Override
	public Entity cd(String obj) {
		return null;
	}

	@Override
	protected void listContent() {
		display();
	}

	@Override
	public int numberOfChildren() {
		return 0;
	}

	@Override
	public byte[] pack() {
		byte[] entityCode 	= {FILE};
		byte[] dirBytes 	= ByteStream.join(entityCode,packBaseData());
		return ByteStream.join(dirBytes,  ByteStream.intToByteArray(0));
	}
}
