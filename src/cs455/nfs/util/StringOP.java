package cs455.nfs.util;

public class StringOP {
	public static String[] parseCommand(String command){
		String[] para = new String[10];
		int idx=0;
		for (;;++idx){
			int loc = command.indexOf(" ") ;
			if(loc==-1)
				break;
			para[idx] = command.substring(0, loc).trim();
			command = command.substring(loc).trim();
		}
		para[idx] = command.trim();
		return para;
	}

}
