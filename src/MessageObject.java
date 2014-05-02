/******************************************************************
 * MessageObject
 * 
 * author: 52665
 * release date: 28 May 2011
 * 
 * This class creates the object which is used for sending commands
 * to and from server. It makes use of Java serialisation to be
 * able to preserver state of the object when sent through the
 * sockets.
 * This class also performs file name directory checking and
 * reads any file to a byte array. The file is then reconstructed
 * at the receivers end.
 ******************************************************************/

import java.io.*;
	
public class MessageObject implements Serializable
{
	private String message;
	private File[] listOfFiles;
	private byte[] fileArray;
	private String nameOfSavedFile;
	private String requestedRemoteFileName;
	
	// constructor
	public  MessageObject(String str)
	{
		message = str;
	}
	
	// getters and setters
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public String getNameOfRemoteFile()
	{
		return requestedRemoteFileName;
	}
	
	public void setRequestedRemoteFileName(String requestedRemoteFileName)
	{
		this.requestedRemoteFileName = requestedRemoteFileName;
	}
		
	
	// list files in current directory
	public void listDirFiles()
	{
		File folder = new File(".");
		try
		{
			folder.getCanonicalPath();
		}
		
		catch (Exception e)
		{
	        e.printStackTrace();
	    }
		
		listOfFiles = folder.listFiles(); 
	}
	
	
	// return list of files in directory object
	public File[] getListOfDirFiles()
	{
		return listOfFiles;
	}
	
	
	// compare file names in directory to user input
	public String compareDirFileNames(String userInput)
	{
		listDirFiles();
		String filename = "file not found";
    	for (int i = 0; i < listOfFiles.length; i++) 
		{
    		if (listOfFiles[i].getName().equals(userInput))	
    			filename = listOfFiles[i].getName();
		}
    	return filename;
	}
	
	// read the file to byte array
	public void readFile(String filename) throws IOException
	{
      File file = new File (filename);
      fileArray = new byte[(int)file.length()];
      FileInputStream fis = new FileInputStream(file);
      fis.read(fileArray);  
      fis.close();
      nameOfSavedFile = filename;
	}
	
	// save the file by writing into it content from byte array
	public void saveFile() throws IOException
	{
		FileOutputStream fos = new FileOutputStream(nameOfSavedFile);
	    fos.write(fileArray);
	    fos.close();
	}
}
