package cs455.nfs.messagehandlers;

import java.io.File;

import cs455.nfs.directorystructure.Entity;
import cs455.nfs.directorystructure.VDirectory;
import cs455.nfs.directorystructure.VFile;
import cs455.nfs.remote.DirectoryService;
import cs455.nfs.wireformates.Message;

public class RetrieveDirectoryHandler implements Handler{

	private void CreateVDS(File node, Entity vNode){
		Entity vEntity;

		if(node.isDirectory()){
			vEntity 			= new VDirectory(node.getName());
			String[] subNote 	= node.list();
			for(String filename : subNote){
				CreateVDS(new File(node, filename),vEntity);
			}		
		}else {
			vEntity = new VFile(node.getName());
		}	
		
		vEntity.setHostIP(DirectoryService.HOST_NAME);
		vEntity.setHostPort(DirectoryService.PORT_NUMBER);
		vNode.add(vEntity);
	}
	
	protected Entity getDS(String pathname){
		Entity 	root 		= new VDirectory("/");
		File 	requiredDir = new File(pathname);
		if (!requiredDir.exists())
			return null;
		CreateVDS(requiredDir, root);
		root = ((VDirectory) root).getChild(0);
		return root;
	}
	
	@Override
	public Message handle(Message msg) {
		return null;
	}

}
