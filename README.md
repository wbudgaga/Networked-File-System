# Networked File System (NFS)
Developing a networked file system that enables transparent access to directories and files residing on remote machines. The implementation includes two components, a directory service that runs on each node and a client module that interacts with multiple remote directory services. The directory service is responsible for managing the physical directory structure and the client module interacts with multiple directory services by constructing a virtual file system on the local machine. The client module supports a set of commands including peek, mount, ls, pwd, mkdir, cd, mv, and rm.


## Compiling and executing:
- To compile the source files you have to execute the command "make all" inside the networkedFileSystem folder.
- To delete class files from the bin folder you have to execute the command "make clean" inside the networkedFileSystem folder.
- To execute the programs you have to go inside the bin folder then execute the the programs as described:
  - To start the DirectoryService:
		java cs455/nfs/remote/DirectoryService portNumber
  - To start the ClientModule:
		 java cs455/nfs/client/ClientModule

