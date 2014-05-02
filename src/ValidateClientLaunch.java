/******************************************************************
 * ValidateClientLaunch
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


public class ValidateClientLaunch
{
	// regex for matching the port number range
	private Pattern portPattern;
	private Matcher portMatcher;
	private boolean portFound;

	// regex for matching the IP or domain range
	private Pattern addressPattern;
	private Matcher addressMatcher;
	private boolean addressFound;
	
	// constructor
	public ValidateClientLaunch()
	{
		portPattern = Pattern.compile("(102[4-9]|10[3-9][0-9]|1[1-9][0-9]{2}|[2-9][0-9]{3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-6])");
		addressPattern = Pattern.compile(".*");
	}
	

	// validate user's input to successfully start the client
	public void validateInput(FSClient fsc, String[] args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				System.out.println("\nArguments missing!\nTo start the client type \"fsc <IP or domain> <port number>\".");
				System.exit(1);
				break;

			case 3:
				String address = args[1];
				addressMatcher = addressPattern.matcher(address);
				addressFound = addressMatcher.matches();

				String port = args[2];
				portMatcher = portPattern.matcher(port);
				portFound = portMatcher.matches();

				if ((!args[0].equals("fsc")) || (!addressFound)|| (!portFound))
				{
					System.out.println("\nClient command, address or port are incorrect!");
					System.out.println("To start the client type \"fsc <IP or domain> <port number>\".");
					System.exit(1);
				}
				else
				{
					System.out.println("\nConnecting...");
					fsc.runClient(address, Integer.parseInt(port));
				}
				break;

			default:
				System.out.println("\nWrong arguments!\nTo start the client type \"fsc <IP or domain> <port number>\".");
				System.exit(1);
		}
	}
}
