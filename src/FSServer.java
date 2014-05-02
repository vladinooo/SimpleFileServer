/******************************************************************
 * FSServer
 * 
 * author: 52665
 * release date: 28 May 2011
 * 
 * This class makes a new thread and runs the server in it each time
 * new client makes a request.
 ******************************************************************/

import java.net.*;
import java.io.*;

public class FSServer
{
	ServerSocket serverSocket = null;
	boolean listening = true;
	private ValidateServerLaunch validServLaunch;
	
	// constructor
	public FSServer()
	{
		validServLaunch = new ValidateServerLaunch();
	}
	
	// main
	public static void main(String[] args) throws IOException
	{
		FSServer fss = new FSServer();
		fss.validServLaunch.validateInput(fss, args);	
	}
	
	// server starting point
	public void runServer(int port) throws IOException
	{
		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			System.err.println("Could not listen on port: " + port);
			System.exit(-1);
		}

		while (listening)
			new FSServerThread(serverSocket.accept()).start(); // create new thread

		serverSocket.close();
	}
	
}
