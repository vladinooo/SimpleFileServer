import java.net.*;
import java.util.regex.Pattern;
import java.io.*;

public class FSClient
{
	// client states
	private static final int CONNECTING = 0;
	private static final int REQUESTING = 1;
	
	private int state = CONNECTING;
		
	private ValidateClientLaunch validClientLaunch;
	private Pattern fileNamePattern;
	
	// message object for serialisation
	private MessageObject messageOutToServer;
	private MessageObject messageInFromServer;
	
	// client streams
	private Socket clientSocket = null;
	private OutputStream outToServerStream = null;
	private ObjectOutputStream objectOutToServer = null;
	private InputStream inFromServerStream = null;
	private ObjectInputStream objectInFromServer = null;
	private BufferedReader inFromUser = null;
	
	// constructor
	public FSClient()
	{
		validClientLaunch = new ValidateClientLaunch();
		fileNamePattern = Pattern.compile(".*");
	}

	// main
	public static void main(String[] args) throws IOException
	{
		FSClient fsc = new FSClient();
		fsc.validClientLaunch.validateInput(fsc, args);
	}

	// run client
	public void runClient(String clientAddress, int serverPort) throws IOException
	{
		try
		{
			clientSocket = new Socket(clientAddress, serverPort);
			outToServerStream = clientSocket.getOutputStream();	
			objectOutToServer = new ObjectOutputStream(outToServerStream);
			
			inFromServerStream = clientSocket.getInputStream();
			objectInFromServer = new ObjectInputStream(inFromServerStream);
			
			inFromUser = new BufferedReader(new InputStreamReader(System.in));
			
			// connecting state
			while (state == CONNECTING)
			{
				// sending connection request
				messageOutToServer = new MessageObject("connect_req");
				objectOutToServer.writeObject(messageOutToServer);
				
				// handling forbidden event
				messageInFromServer = (MessageObject)objectInFromServer.readObject();
		    	if (messageInFromServer.getMessage().equals("unauthorised"))
	            {
		    		System.out.println("Server refused connection!");
		    		break;
	            }
		    	// sending password
		    	if (messageInFromServer.getMessage().equals("passwd_req"))
	            {
		    		messageOutToServer = new MessageObject(collectPassword());
		    		objectOutToServer.writeObject(messageOutToServer);
	            }
		    	// handling access permission
		    	messageInFromServer = (MessageObject)objectInFromServer.readObject();
		    	if (messageInFromServer.getMessage().equals("access_ok"))
	            {
		    		System.out.print("Have fun!\n");
					state = REQUESTING;
	            }
						
				
			}
			
			// requesting state
			while (state == REQUESTING)
			{
				messageOutToServer = new MessageObject(inFromUser.readLine());
			    if (messageOutToServer != null)
			    {
			    	// handling lls command
			    	if (messageOutToServer.getMessage().equals("lls")) // message dealt with locally
			    	{
			    		System.out.println("Client> Files in local directory: \n");
			    		messageOutToServer.listDirFiles();
				    	printLocalDirFileList(messageOutToServer.getListOfDirFiles());
			    	}
			    	// sending rls command and dealing with reply
			    	else if (messageOutToServer.getMessage().equals("rls"))
			    	{
				    	objectOutToServer.writeObject(messageOutToServer);
				    	
				    	messageInFromServer = (MessageObject)objectInFromServer.readObject();
				    	if (messageInFromServer.getMessage().equals("rls_ok"))
			            {
			            	System.out.println("Server> Files in remote directory: \n");
			            	printRemoteDirFileList(messageInFromServer.getListOfDirFiles());
			            }
			    	}
			    	// sending put command and dealing with reply
			    	else if (messageOutToServer.getMessage().matches("put " + fileNamePattern)) // message dealt with locally
			    	{
			    		String filename = messageOutToServer.getMessage().substring(4);
			    		if (!messageOutToServer.compareDirFileNames(filename).equals("file not found"))
			    		{
			    			messageOutToServer.readFile(filename);
				    		messageOutToServer.setMessage("put");
				    		objectOutToServer.writeObject(messageOutToServer);
				    		
					    	messageInFromServer = (MessageObject)objectInFromServer.readObject();
					    	if (messageInFromServer.getMessage().equals("put_ok"))
				            {
					    		System.out.println("Server> File received.");
				            }
			    		}
			    		else
			    			System.out.println("Client> File not found!");
			    	}
			    	// sending get command and dealing with reply
			    	else if (messageOutToServer.getMessage().matches("get " + fileNamePattern)) // message dealt with locally
			    	{
			    		String filename = messageOutToServer.getMessage().substring(4);
			    		messageOutToServer.setMessage("get");
			    		messageOutToServer.setRequestedRemoteFileName(filename);
			    		objectOutToServer.writeObject(messageOutToServer);
			    		
				    	messageInFromServer = (MessageObject)objectInFromServer.readObject();
				    	if (messageInFromServer.getMessage().equals("get_ok"))
			            {
				    		messageInFromServer.saveFile();
				    		System.out.println("Client> File received.");
			            }
				    	else if (messageInFromServer.getMessage().equals("get_not_found"))
				    	{
				    		System.out.println("Server> File not found!");
				    	}
			    	}
			    	// sending exit command and dealing with reply
			    	else if (messageOutToServer.getMessage().equals("exit"))
			    	{
				    	objectOutToServer.writeObject(messageOutToServer);
				    	
				    	messageInFromServer = (MessageObject)objectInFromServer.readObject();
				    	if (messageInFromServer.getMessage().equals("exit_ok"))
			            {
			            	System.out.println("Server> Good Bye!\n");
			            	break;
			            }
			    	}
			    	// sending unknown command and dealing with reply
			    	else
			    	{
			    		objectOutToServer.writeObject(messageOutToServer);
				    	
				    	messageInFromServer = (MessageObject)objectInFromServer.readObject();
				    	if (messageInFromServer.getMessage().equals("unknown_command"))
			            {
				    		System.out.println("Server> Unknow command!\n");
			            }
			    	}
			    		
			    }           
	            
		    }
			
		}
		catch (UnknownHostException e)
		{
			System.err.println("Could not connect to server: " + clientAddress);
			System.exit(1);
		}
		catch (IOException e)
		{
			System.err.println("Connection failed!\nServer: " + clientAddress + " is probably not running or not listening on port " + serverPort);
			System.exit(1);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		try
		{
			objectOutToServer.close();
			outToServerStream.close();
			objectInFromServer.close();
			inFromServerStream.close();
			clientSocket.close();
			System.exit(1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	// ask for password
	private String collectPassword()
	{
		System.out.println("Password:");
		String password = "";
		try
		{
			password = inFromUser.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return password;
	}
	
	
	// list and print file names of client's local directory
	private void printLocalDirFileList(File[] listOfFiles)
	{
		String files;
    	for (int i = 0; i < listOfFiles.length; i++) 
		{
    		files = listOfFiles[i].getName();
			System.out.println(files);	
		}
	}
	
	
	// take serialised object received from server
	// and print list of files in remote directory
	private void printRemoteDirFileList(File[] listOfFiles)
	{
    	String files;
    	for (int i = 0; i < listOfFiles.length; i++) 
		{
    		files = listOfFiles[i].getName();
			System.out.println(files);	
		}
	}
}
