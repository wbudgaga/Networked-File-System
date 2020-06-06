package cs455.nfs.wireformates;

import cs455.nfs.directorystructure.VDirectory;
import cs455.nfs.directorystructure.Entity;
import cs455.nfs.directorystructure.VFile;

//it is a supper class of RegisterRequest and DeRegisterRequest 
public class RetrieveDS extends Message{
	private Entity	root;
	
	public RetrieveDS(int id) {
		super(id);
		root = null;
	}
	
	private Entity unpackEntity(byte[] byteStream){
		Entity 	entity;
		byte 	entityCode 	= unpackByteField(byteStream);
		String 	name		= unpackStringField(byteStream);
		if (entityCode == 1)
			entity = new VFile(name);
		else
			entity = new VDirectory(name);
		entity.setHostIP  (unpackStringField(byteStream));
		entity.setHostPort(unpackIntField   (byteStream));
		return entity;
	}
	
	private Entity unpackDS(byte[] byteStream){
		Entity entity = unpackEntity(byteStream);
		int numberOfChildren = unpackIntField(byteStream);
		for (int i=0; i<numberOfChildren; ++i){
			Entity child = unpackDS(byteStream);
			entity.add(child);
		}
		return entity;
	}
	
	@Override
	public void initiate(byte[] byteStream) {
		currentIndex = 4;
		if(byteStream.length== 4) 
			root = null;
		else
			root =  unpackDS(byteStream);
	}

	@Override
	protected byte[] packMessageBody() {
		if (root== null)
			return null;
		return root.pack();
	}

	public Entity	getRoot(){
		return root;
	}
	
	public void	setRoot(Entity e){
		e.setParent(null);
		root = e;
	}

}
