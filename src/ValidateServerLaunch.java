/******************************************************************
 * ValidateServerLaunch
 * 
 * author: 52665
 * release date: 28 May 2011
 * 
 * This class performs validation on user's input from the console.
 * If there are missing or wrong parameters, error messages are thrown.
 ******************************************************************/

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidateServerLaunch
{
	// regex for matching the port number range
	private Pattern portPattern;
	private Matcher portMatcher;
	private boolean portFound;
	
	// constructor
	public ValidateServerLaunch()
	{
		portPattern = Pattern.compile("(102[4-9]|10[3-9][0-9]|1[1-9][0-9]{2}|[2-9][0-9]{3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-6])");
	}

	
	// validate user's input to successfully start the server
	public void validateInput(FSServer fss, String[] args) throws IOException
	{
		switch (args.length)
		{
			case 0:						
				System.out.println("\nArguments missing!\nTo start the server type \"fss <port number between 1024-65536>\".");
				System.exit(1);
				break;
				
			case 1:
				if (!args[0].equals("fss"))
					System.out.println("\nServer command is invalid! Must be \"fss\"");
				else
				{
					System.out.println("\nPort number missing!");
					System.exit(1);
				}
				break;
				
			case 2:
				if (args[0].equals("fss"))
				{
					String port = args[1];
					portMatcher = portPattern.matcher(port);
					portFound = portMatcher.matches();
					if (portFound)
					{
						int portNumber = Integer.parseInt(port);
						System.out.println("\nWelcome!\nServer is running and waiting for requests from client...\n");
						fss.runServer(portNumber);
					}
					else
					{
						System.out.println("\nPort number is invalid or already in use!");
						System.exit(1);
					}
				}
				else
				{
					System.out.println("\nTo start the server type \"fss <port number between 1024-65536>\".");
					System.exit(1);
				}
				break;
				
			default:
				System.out.println("\nToo many arguments!\nTo start the server type \"fss <port number between 1024-65536>\".");
				System.exit(1);
		}
	}
}
