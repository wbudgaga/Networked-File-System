package cs455.nfs.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cs455.nfs.directorystructure.VDirectory;
import cs455.nfs.directorystructure.Entity;
import cs455.nfs.directorystructure.VFile;
import cs455.nfs.wireformates.RetrieveDS;

public class DirectoryStructureTest {
	private 	BufferedReader 		bufferedReader;	
	
	public Entity root;
	
	public DirectoryStructureTest(){
		bufferedReader 	= new BufferedReader(new InputStreamReader(System.in));
		root = new VDirectory("/");
		root.setHostIP("twoHostIP");
		root.setHostPort(2);
	}
	
	public void create(){	
        VDirectory one = new VDirectory("dir1"), two = new VDirectory("dir2"), thr = new VDirectory("dir3"), four = new VDirectory("dir4");
        Entity a = new VFile("file_a"), b = new VFile("file_b"), c = new VFile("file_c"), d = new VFile("file_d"), e = new VFile("file_e");
        one.setHostIP("oneHostIP");
        one.setHostPort(1);
        two.setHostIP("twoHostIP");
        two.setHostPort(2);
        thr.setHostIP("thrHostIP");
        thr.setHostPort(3);
        four.setHostIP("fourHostIP");
        four.setHostPort(4);
       
        a.setHostIP("aHostIP");
        a.setHostPort(52);
        b.setHostIP("twoHostIP");
        b.setHostPort(2);
        c.setHostIP("twoHostIP");
        c.setHostPort(2);
        d.setHostIP("twoHostIP");
        d.setHostPort(2);
        e.setHostIP("twoHostIP");
        e.setHostPort(2);
 
        one.add(a);
        one.add(two);
        one.add(b);
        two.add(c);
        two.add(d);
        two.add(thr);
        thr.add(e);
//        root.add(new VDirectory("first directory"));
        root.add(one);
        root.add(four);
	}
	
	private String parseDirName(String command){
		return command.substring(3).trim();
	}

	private String readCommand(){
		try {
			return bufferedReader.readLine();
		} catch (IOException e) {}
		return "quit";
	}
	
	public void commandLineReanderMainLoop() {
    	create();
		RetrieveDS msg = new RetrieveDS(1);
		Entity curDir = root;
		String command;
		while (true){
			command = readCommand();
			if (command == null)															continue;
			if(command.compareTo("ls")	==0)	{curDir.ls(); 								continue;}
			if(command.contains("pwd"))			{curDir.pwd(); 								continue;}
			if(command.compareTo("cd..")==0)	{curDir = curDir.cd(); 						continue;}
			if(command.contains("cd ")	)	    {curDir = curDir.cd(parseDirName(command));	continue;}
			
			if(command.contains("pack")	)	    {
												msg.setRoot(curDir);
												byte[] p = msg.packMessage();
												RetrieveDS msg1 = new RetrieveDS(1);
												msg1.initiate(p);
												System.out.println(p); 	
												msg1.getRoot().ls();							continue;}
			System.out.println("invalid command!");
		}
	}


	
    public static void main(String[] args){
    	DirectoryStructureTest nfs = new DirectoryStructureTest();
    	nfs.commandLineReanderMainLoop();
     }

}
