package cs455.nfs.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cs455.nfs.directorystructure.VDirectory;
import cs455.nfs.directorystructure.Entity;
import cs455.nfs.util.StringOP;


public class ClientModule {
	private BufferedReader 		bufferedReader;		
	private Entity 				root , curDir;
	private TransmissionHandler handler;

	public ClientModule() {
		handler 		= new TransmissionHandler();
		bufferedReader	= new BufferedReader(new InputStreamReader(System.in));
		curDir = root	= createDir("/");
	}
	
	/*
	 * createDir: create local virtual directory			
	 */
	private Entity createDir(String dirName){
		Entity dir = new VDirectory(dirName);
		dir.setHostIP(Entity.LOCAL_HOST);
		dir.setHostPort(Entity.LOCAL_PORT);
		return dir;
	}
	
	/*
	 * vrm: removes file or directory only from virtual structure(not from remote directory)
	 *      it supports multi-level deletion. e.g. vrm c/b/f will delete f from b
	 */
	private void vrm(String command){
		String[] 	para 		= StringOP.parseCommand(command);
		Entity		parentNode	= checkValidityOfPath(para[1],1);
		String[]  	pathParts	= para[1].split("/");
		if (parentNode == null || pathParts.length==0)
			System.out.println(para[1]+": No such file or directory to remove!");
		else
			parentNode.remove(pathParts[pathParts.length - 1]);
	}	
	
	/*
	 * vcd: traverses from current directory to the given one
	 *      it supports multi-level traversing. e.g. vcd c/b/f will let the current directory f
	 */
	private void vcd(String command){
		String[] para = StringOP.parseCommand(command);
		if (para[1]==null || para[1].isEmpty()){
			System.out.println("invalid command's parameter!");
			return;
		}
		String[]pathParts 	= para[1].split("/");
		Entity 	parentNode 	= checkValidityOfPath(para[1],1);
		if (parentNode == null){
			System.out.println(para[1]+": could not be found!");
			return;
		}
		if (pathParts.length > 0)
			curDir = parentNode.cd(pathParts[pathParts.length - 1]);
	}	
	
	/*
	 * peek: calls the peek method in handler(of type TransmissionHandler class). 
	 * The second peek method will send peek request to the given server and then returns the root directory to the first peek which executes ls command
	 */
	private void peek (String command) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		String[] para = StringOP.parseCommand(command);
		if (para[1]==null || para[1].isEmpty() || para[2]==null ||  para[2].isEmpty() ){
			System.out.println("Invalid command parameters!");
			return;
		}
		try {
			int port = Integer.parseInt(para[2]);
			Entity root = handler.peek(para[1], port);
			root.ls();
		} catch (NumberFormatException e) {
			System.out.println("Invalid expected port number!");
		}
	}
	
	/*
	 * vmount: calls the vmount method in handler(of type TransmissionHandler class). 
	 * The second vmount method will send vmount request containing required path to the given server
	 * and then returns the root of that path(if found) or null(not found) to the first peek which appropriate message
	 */
	private void vmount (String command) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		String[] para = StringOP.parseCommand(command);
		if (para[1]==null || para[1].isEmpty() || para[2]==null ||  para[2].isEmpty() ){
			System.out.println("Invalid command parameters!");
			return;
		}
		try {
			int 	port = Integer.parseInt(para[2]);
			if (para[3]==null ||  para[3].isEmpty()){
				System.out.println("Destination directory must be given!");
				return;
			}
			Entity 	remoteDir = handler.mount(para[1], port,para[3]);
			if (remoteDir==null)
				System.out.println("vmount: cannot find '"+para[3]+"': No such file or directory!");
			else
				((VDirectory)curDir).addChildren(remoteDir);
		} catch (NumberFormatException e) {
			System.out.println("Invalid expected port number!");
		}
	}
	
	/*
	 * checkValidityOfPath: receives directory (pathDir) as string and builds a complete directory 
	 * 					    path from curDir (or root if the first element is "/") to the last element or
	 * 						the one before the last element( depending on deepStatus) in pathDir.
	 * 						The method returns the entity node of the last element if the elements in pathDir are valid, 
	 * 			            otherwise it returns null.
	 */
	private Entity checkValidityOfPath(String pathDir, int deepStatus){//  deepStatus = 0 means from curDir till leaf. deepStatus = 1 means from curDir till leaf's parent
		String[] pathParts = pathDir.split("/");

		if (pathParts == null || pathParts.length < 2 ){
			if (deepStatus==0)
				return ((VDirectory)curDir).getChild(pathDir);
			else
				return curDir;
		}
		Entity completeDir;
		int index = 0;
		if (pathParts[0].compareTo("")==0){
			completeDir = root;
			++index;
		}else
			completeDir = curDir;
		for (int i = index; i< pathParts.length - deepStatus; ++i){
			if (completeDir.equals(completeDir.cd(pathParts[i])))
				return null;
			completeDir = completeDir.cd(pathParts[i]);
		}
		return completeDir;
	}
	
	/*
	 * vmv: moves a file from particular directory to another. It supports moving of files in the same 
	 *      physical directory(on the same server) and in different physical directories(on different servers).
	 */
	private void vmv (String command) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		String[] para = StringOP.parseCommand(command);
		if (para[1]==null || para[1].isEmpty() || para[2]==null ||  para[2].isEmpty() ){
			System.out.println("Invalid command parameters!");
			return;
		}
		String[]	srcDirFile 	= para[1].split("/");
		String 		file 		= srcDirFile[srcDirFile.length - 1];
		Entity		srcEntity	= checkValidityOfPath(para[1],1);
		Entity		dstEntity	= checkValidityOfPath(para[2],0);
		if (srcEntity == null || dstEntity == null)
			return;

		Entity 		fileEntity	= ((VDirectory)srcEntity).getChild(file);
		if (fileEntity == null || !fileEntity.isFile()){
			System.out.print(file+": No such file in  ");
			srcEntity.pwd();
			return;
		}
		
		if (dstEntity.isLocal()){
			System.out.println("it could not move file("+file+") because the distination dir("+dstEntity.getName()+") is virtual directory!");
			return;
		}
		if (!dstEntity.canAdd(file))
			System.out.println(file+": could not be created in '"+ para[2]+"' because it is already found or has illegal name!");
		else
			handler.mv(fileEntity, dstEntity);
	}

	/*
	 * vmkdir: creates directory in another directory. it creates directory in virtual structure 
	 *         and in remote machine if the parent directory is located remotely.
	 * 		  It supports multi-level creation of directories. e.g. vmkdir a/b/c creates c in b if b exists
	 */
	private void vmkdir(String command) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		String[] para = StringOP.parseCommand(command);
		if (para[1]==null || para[1].isEmpty()){
			System.out.println(para[1]+": could not be created because it has illegal name!");
			return;
		}
		
		String[]	pathParts 	= para[1].split("/");
		Entity 		parentNode 	= checkValidityOfPath(para[1],1);
		String 		dirName 	= pathParts[pathParts.length - 1];
		if (parentNode == null || !parentNode.canAdd(dirName) ){
			System.out.println(para[1]+": could not be created because the distination path is incorrect!");
			return;
		}
			
		if (parentNode.isLocal())
			parentNode.add(createDir(dirName));
		else
			handler.mkdir(parentNode, dirName);
	}	
	
	private String readCommand(){
		try {
			return bufferedReader.readLine();
		} catch (IOException e) {}
		return "quit";
	}

	private void start() throws  InstantiationException, IllegalAccessException, ClassNotFoundException {
		String command;
		while (true){
			try {
				command = readCommand();
				if (command == null)						{						continue;}
				if (command.startsWith	("peek ")		)   {peek	(command);		continue;}
				if (command.startsWith 	("vmount ")		)	{vmount	(command);		continue;}
				if (command.startsWith 	("vmv ")		)	{vmv	(command);		continue;}
				if (command.startsWith 	("vmkdir ")		) 	{vmkdir	(command);		continue;}
				if (command.startsWith 	("vrm ")		) 	{vrm	(command);		continue;}
				if (command.compareTo  	("vls")	==0		)	{curDir.ls(); 			continue;}
				if (command.compareTo	("vpwd")	==0	)	{curDir.pwd(); 			continue;}
				if (command.compareTo	("vcd..")	==0	)	{curDir = curDir.cd(); 	continue;}
				if (command.compareTo	("vcd ..")	==0	)	{curDir = curDir.cd(); 	continue;}
				if (command.startsWith 	("vcd ")		)   {vcd	(command);		continue;}
				if (command.compareTo	("quit")	==0	)	{						break;	 }
				
				System.out.println("invalid command!");
			}catch (IOException e){
				System.err.println(e.getMessage());
			}
		}
	}

	public static void main(String args[]){
		ClientModule client = new ClientModule();
		try {
			client.start();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
	}
}
