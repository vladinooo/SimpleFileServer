/******************************************************************
 * FSServerThread
 * 
 * author: 52665
 * release date: 28 May 2011
 * 
 * This is the main server class where all logic is performed.
 * Copy of this class's object is made every time new thread is
 * created. For details of how it works see comments in the code
 * and documentation included. 
 ******************************************************************/

import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

public class FSServerThread extends Thread
{
	private static final String passwd = "12345";
	
	// server states
	private static final int AUTHENTICATING = 0;
	private static final int SERVING = 1;	
	
	private int state;
	private boolean running;
	
	private ForbiddenCheck fch;
	private int passwdReq = 0;

	// message objects used for serialisation
	private MessageObject messageOutToUser;
	private MessageObject messageInFromUser;
	
	// sockets and streams
	private Socket clientSocket = null;
	private InputStream inFromClientStream = null;
	private ObjectInputStream objectInFromClient = null;
	private OutputStream outToClientStream = null;
	private ObjectOutputStream objectOutToClient = null;
	
	// constructor
	public FSServerThread(Socket clientSocket) throws IOException
	{
		super("FSServerThread");
		this.clientSocket = clientSocket;
		fch = new ForbiddenCheck();
		
		try
		{
			inFromClientStream = clientSocket.getInputStream();
			objectInFromClient = new ObjectInputStream(inFromClientStream);
			
			outToClientStream = clientSocket.getOutputStream();
			objectOutToClient = new ObjectOutputStream(outToClientStream);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
		
	// run method used by thread
	// this method receives and processes client requests and it maintains two
	// server states.
	public void run()
	{
		running = true;
		
		while (running)
		{
			// authenticating client
			while (state == AUTHENTICATING)
			{
				try
				{
					// handle initial connection request
					messageInFromUser = (MessageObject)objectInFromClient.readObject();
					if (messageInFromUser.getMessage().equals("connect_req"))
					{
						// check if client is forbidden
						if (fch.verifyClient(clientSocket.getInetAddress().toString()))
						{
							forbiddenMessage(clientSocket.getInetAddress(), clientSocket.getLocalPort());
							messageOutToUser = new MessageObject("unauthorised");
							objectOutToClient.writeObject(messageOutToUser);
							running = false;
							clientSocket.close();
							break;
						}
						// request password up to 3x
						else if (passwdReq <= 2) 
						{
							connectionMessage(clientSocket.getInetAddress(), clientSocket.getLocalPort());
							messageOutToUser = new MessageObject("passwd_req");
							objectOutToClient.writeObject(messageOutToUser);
							passwdReq++;
						}
						// refuse client after 3x wrong password received
						else
						{
							wrongPasswdMessage(clientSocket.getInetAddress(), clientSocket.getLocalPort());
							messageOutToUser = new MessageObject("unauthorised");
							objectOutToClient.writeObject(messageOutToUser);
							passwdReq = 0;
							running = false;;
							clientSocket.close();
							break;
						}
					}
					// allow access after correct password is received, request again otherwise
					messageInFromUser = (MessageObject)objectInFromClient.readObject();
					if (messageInFromUser.getMessage().equals(passwd))
					{
						authenticationMessage(clientSocket.getInetAddress(), clientSocket.getLocalPort());
						messageOutToUser = new MessageObject("access_ok");
						objectOutToClient.writeObject(messageOutToUser);
						state = SERVING;
					}
					else
					{
						messageOutToUser = new MessageObject("passwd_req");
						objectOutToClient.writeObject(messageOutToUser);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}
			}
			
			// serving to clients requests
			while (state == SERVING)
			{	
				try
				{
					// process exit command
					messageInFromUser = (MessageObject)objectInFromClient.readObject();
					if (messageInFromUser.getMessage().equals("exit"))
					{
						disconnectMessage(clientSocket.getInetAddress(), clientSocket.getLocalPort());
						messageOutToUser = new MessageObject("exit_ok");
						objectOutToClient.writeObject(messageOutToUser);
						running = false;;
						clientSocket.close();
						break;
					}
					// process rls command
					else if (messageInFromUser.getMessage().equals("rls"))
					{
						messageOutToUser = new MessageObject("rls_ok");
						messageOutToUser.listDirFiles();
						objectOutToClient.writeObject(messageOutToUser);
					}
					// process put command
					else if (messageInFromUser.getMessage().equals("put"))
					{
						messageInFromUser.saveFile();
					    messageOutToUser = new MessageObject("put_ok");
						objectOutToClient.writeObject(messageOutToUser);
					}
					// process get command
					else if (messageInFromUser.getMessage().equals("get"))
					{
						String filename = messageInFromUser.getNameOfRemoteFile();
						if (!messageOutToUser.compareDirFileNames(filename).equals("file not found"))
						{
							messageOutToUser = new MessageObject("get_ok");
						    messageOutToUser.readFile(filename);
							objectOutToClient.writeObject(messageOutToUser);
						}
						else
						{
							messageOutToUser = new MessageObject("get_not_found");
							objectOutToClient.writeObject(messageOutToUser);
						}
			    			
					}
					// process unknown command
					else
					{
					    messageOutToUser = new MessageObject("unknown_command");
						objectOutToClient.writeObject(messageOutToUser);
					}
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}	
				
			}
		}
		
		try
		{
			objectInFromClient.close();
			inFromClientStream.close();
			objectOutToClient.close();
			outToClientStream.close();
			clientSocket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	// get acknowledgement when client first connects
	private void connectionMessage(InetAddress address, int port)
	{
		System.out.println(getDateTime() + " Client " + address + " with port " + port + " requesting to connect...");
	}
	
	// get acknowledgement when client authenticated
	private void authenticationMessage(InetAddress address, int port)
	{
		System.out.println(getDateTime() + " Client " + address + " with port " + port + " successfully connected.");
	}
	
	// get acknowledgement when client is forbidden
	private void forbiddenMessage(InetAddress address, int port)
	{
		System.out.println(getDateTime() + " Client " + address + " with port " + port + " is forbidden.");
	}
	
	// get acknowledgement when client disconnects
	private void disconnectMessage(InetAddress address, int port)
	{
		System.out.println(getDateTime() + " Client " + address + " with port " + port + " disconnected.");
	}
	
	// get acknowledgement when client sends wrong password 3x
	private void wrongPasswdMessage(InetAddress address, int port)
	{
		System.out.println(getDateTime() + " Client " + address + " with port " + port + " sent wrong password.");
	}
	
	
	// get date and time
	private static String getDateTime()
	{
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


}
