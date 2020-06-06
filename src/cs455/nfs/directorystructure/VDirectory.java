package cs455.nfs.directorystructure;

import java.util.ArrayList;

import cs455.nfs.util.ByteStream;

public class VDirectory extends Entity{
	private ArrayList<Entity> children = new ArrayList<Entity>();
	
	public VDirectory(String name) {
		super(name);
		entityCode = DIRECTORY;
	}

	public boolean canAdd(String entityToBeAdded){
		return getChild(entityToBeAdded)==null;
	}

	@Override
	public void add(Entity obj) {
		obj.parent = this;
		children.add(obj);
	}
	public 	void remove(String name){
		Entity child = getChild(name);
		if (child!=null){
			children.remove(child);
		}else
			System.out.println("vrm: cannot remove '"+name+"': No such file or directory");
	}
	
	@Override
	public void remove(Entity obj) {	
		remove(obj.name);
	}

	protected void display(){
		System.out.println(indent.toString() +"+---"+ name);
	}

	protected void listContent() {
		display();
        indent.append("|   ");
        for(Entity obj:children)
            obj.listContent();
        
        indent.setLength(indent.length() - 4);	
   }

	@Override
	public void ls() { 
        for(Entity obj:children)
            obj.listContent();
   }

	@Override
	public byte[] pack() {
		byte[] entityCode 	= {DIRECTORY};
		byte[] dirBytes 	= ByteStream.join(entityCode,packBaseData());
		dirBytes			= ByteStream.join(dirBytes,  ByteStream.intToByteArray(children.size()));
		for(Entity child:children){
			dirBytes = ByteStream.join(dirBytes, child.pack());
		}
		return dirBytes;
	}

	@Override
	public Entity cd() {
		if (parent!=null)
			return parent;
		return this;
	}

	@Override
	public Entity cd(String name) {
		Entity child = getChild(name);
		if (child!=null)
			return child;
		System.out.println(name+": No such directory");
		return this;
	}

	@Override
	public int numberOfChildren() {
		return children.size();
	}
	
	public Entity getChild(String name){
		for(Entity child:children)
			if (child.name.compareTo(name)==0)
				return child;
		return null;
	}


	public Entity getChild(int index) {
		return children.get(index);
	}
	
	public void addChildren(Entity node) {
		if (node.isFile())	return;
		for(Entity child:((VDirectory)node).children)
			add(child);
	}

}
