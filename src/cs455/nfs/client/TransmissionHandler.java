package cs455.nfs.client;

import java.io.IOException;

import cs455.nfs.connection.DataTransfer;
import cs455.nfs.directorystructure.Entity;
import cs455.nfs.directorystructure.VDirectory;
import cs455.nfs.wireformates.MessageType;
import cs455.nfs.wireformates.MkdirRequest;
import cs455.nfs.wireformates.MountRequest;
import cs455.nfs.wireformates.MountResponse;
import cs455.nfs.wireformates.PeekRequest;
import cs455.nfs.wireformates.PeekResponse;
import cs455.nfs.wireformates.RelocationRequest;
import cs455.nfs.wireformates.Response;


public class TransmissionHandler{
	public  TransmissionHandler() {}

	 protected Entity peek(String ip, int port) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		PeekRequest 	msg 			= new PeekRequest();
		PeekResponse 	peekResponse 	= (PeekResponse) DataTransfer.sendMessage(msg,ip,port);
		return peekResponse.getRoot();
	 }
	 
	 protected Entity mount(String ip, int port, String dirPath)throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {;
		MountRequest 	msg 			= new MountRequest(dirPath);
		MountResponse 	mountResponse 	= (MountResponse) DataTransfer.sendMessage(msg,ip,port);
		return mountResponse.getRoot();
	 }
	 
	 private void virtualStructureMV(Entity fileEntity, Entity dst){
		 Entity parent = fileEntity.getParent();
		 fileEntity.setHostIP(dst.getHostIP());
		 fileEntity.setHostPort(dst.getHostPort());
		 parent.remove(fileEntity);
		 dst.add(fileEntity);
	 }
	 	 
	 private void virtualStructureMkdir(Entity parent, String childName){
		Entity dir = new VDirectory(childName);
		dir.setHostIP(parent.getHostIP());
		dir.setHostPort(parent.getHostPort());
		parent.add(dir);
	 }

	 protected void mv(Entity fileEntity, Entity dstEntity) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		 System.out.println("the vmv operation has been started to move file from dir to another." +
		 		"\n (you can change the buffer size in Setting interface to affect the transfer's speed of files base on their sizes)\n\n");
		 RelocationRequest 	msg	= new RelocationRequest();
		 msg.setFileName	(fileEntity.getName());
		 msg.setSrcDir		(Entity.getFilePath(fileEntity));
		 msg.setDstDir		(Entity.getFilePath(dstEntity));
		 msg.setDstHostIP	(dstEntity.getHostIP());
		 msg.setDstPort		(dstEntity.getHostPort());
		 Response 	response 	= (Response) DataTransfer.sendMessage(msg, fileEntity.getHostIP(),fileEntity.getHostPort());
		 if (response.getStatusCode()==MessageType.TRUE){
			 virtualStructureMV(fileEntity, dstEntity);
		 }
		 System.out.println(response.getAdditionalInfo());
	 }
	 
	 protected void mkdir(Entity parent, String childName) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{			 
		 MkdirRequest 	msg	= new MkdirRequest();
		 msg.setDir		(childName);
		 msg.setDstDir	(Entity.getFilePath(parent));
		 Response response  = (Response) DataTransfer.sendMessage(msg, parent.getHostIP(),parent.getHostPort());
		 if (response.getStatusCode()==MessageType.TRUE){
			 virtualStructureMkdir(parent, childName);
		 }
		 System.out.println(response.getAdditionalInfo());
	 }
}
