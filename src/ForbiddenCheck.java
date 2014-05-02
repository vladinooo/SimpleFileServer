/******************************************************************
 * ForbiddenCheck
 * 
 * author: 52665
 * release date: 28 May 2011
 * 
 * This class is concerned with checking whether the connecting client
 * is in forbidden.txt record.
 ******************************************************************/

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;


public class ForbiddenCheck
{
	private ArrayList<String> forbiddenRecords;
	
	// constructor
	public ForbiddenCheck()
	{
		forbiddenRecords = new ArrayList<String>();
		readForbiddenFile();
	}
	
	// read forbidden.txt and store each line in ArrayList
	private void readForbiddenFile()
	{
		try
		{
			FileInputStream fis = new FileInputStream("forbidden.txt");
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String oneLine;

			while ((oneLine = br.readLine()) != null)
			{
				// add to arraylist for later manipulation
				forbiddenRecords.add(oneLine);
			}
			fis.close();
			dis.close();
			br.close();
			
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	// iterate through ArrayList and compare records against
	// client requesting connection
	public boolean verifyClient(String address)
	{
		boolean isForbidden = false;
		Iterator<String>  it = forbiddenRecords.iterator();
		while (it.hasNext())
		{
			if (it.next().equals(address))
				isForbidden = true;
		}
		
		return isForbidden;
	}
}
