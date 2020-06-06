package cs455.nfs.directorystructure;

import java.io.File;

import cs455.nfs.util.ByteStream;

public abstract class Entity {
	protected 		 final 	byte 			FILE		=1;
	protected 		 final 	byte 			DIRECTORY	=2;
	public	  static final 	byte 			LOCAL_PORT	=0;
	public 	  static final 	String 			LOCAL_HOST	="LOCAL";
	protected static 		StringBuffer 	indent		= new StringBuffer();
	
	protected String 	name;
	protected Entity 	parent;
	protected String	hostIP;
	protected int 		hostPort;
	protected byte 		entityCode;

	public Entity(String name){
		this.name 	= name;
		parent 		= null;
	}

	public boolean isLocal(){
		return (getHostIP().compareTo(LOCAL_HOST) == 00 && getHostPort()==LOCAL_PORT);
	}

	public boolean isFile(){
		return entityCode == FILE;
	}
	
	public boolean canAdd(String entityToBeAdded){
		return false;
	}
	
	protected void display(){
		System.out.println(indent.toString() +"|---"+ name);
	}
	
	private void printMyPath(){
		if (parent != null){
			parent.printMyPath();
			System.out.print("/"+name);
		}
	}
	
	public void pwd(){
		if (parent != null)
			printMyPath();
		else
			System.out.print("/");
		System.out.println();
	}

	protected   byte[] packBaseData(){
		byte[] bytes 	= ByteStream.packString(name);
		bytes 			= ByteStream.join(bytes, ByteStream.packString(hostIP));
		return ByteStream.join(bytes, ByteStream.intToByteArray(hostPort));
	}
	public 		void remove(String object){}
	
	// find entity's path  with the same hostIP
	public static String getFilePath(Entity entity){
		String ip 	= entity.getHostIP();
		String path = entity.getName();
		while (true){
			entity 	= entity.getParent();
			if (entity==null || entity.getHostIP().compareTo(ip)!= 0)
				break;
			path 	= entity.getName()+"/" + path;
		}
		return path;
	}

	// searching in 'searchDir' and its subdirectories to find 'path'. 
	// returns the path as Entity if found or null otherwise
	public static  File findPath(File searchDir, String path){
		if(searchDir.getPath().endsWith(path))
			return searchDir;

		if(searchDir.isDirectory())	
			for(File file : searchDir.listFiles()){
				File result = findPath(file,path);
				if (result!= null)
					return result;
			}
		return null;
	}

	public 		abstract void 				add(Entity obj);
	public 		abstract void 				remove(Entity object);
	public 		abstract void 				ls();
	protected 	abstract void 				listContent();
	public 		abstract Entity 			cd();
	public 		abstract Entity 			cd(String obj);
	public 		abstract int 				numberOfChildren();
	public		abstract byte[]				pack();
	
	public String getHostIP() {
		return hostIP;
	}
	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}
	public int getHostPort() {
		return hostPort;
	}
	public void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}
	public void setParent(Entity p) {
		parent = p;
	}
	
	public Entity getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
