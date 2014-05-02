******************************************************************************

         How to run myFirstFileServer application   (Candidate no: 52665)

******************************************************************************


Program is designed in default package so it can be run from command line
without any long prefixes.

To run the server type: java FSServer fss <port numner>
To run the client type: java FSClient fsc <address> <port number>

(address can be "localhost" and port can be "1981")

Note that compilation with javac might be necessary.

You will get suggesting error messages when you miss or enter wrong parameters.
Once you launch the server and client respectively
(you can launch as many clients as you like) server will check
whether the address of the client is not in forbidden.txt record file.

Check the forbidden.txt for more info on setting IP or FQDN to take the blocking effect.
If address or IP is in forbidden.txt, server will deny access and close the connection.
If it's not, you will be prompted for password.

Password is: 12345

Server will deny access and close the connection to the client
if password was entered 3x incorrectly.
After gaining the connection to the server, you can use these commands:

  lls - list local directory (directory where client program runs)
  rls - list remote directory (directory where server program runs)
  put <filename> - transfers file in client's current directory to the server's current directory
  get <filename> - transfers file from the server's current directory to the client’s current directory
  exit - quits the client

Server can be launched on one machine while multiple clients can access it
from different machines. Server program shows the details of successful/unsuccessful
connections in the console while running.

This Client/Server application can transfer any type of files of any size.


***************************************************************

Following possible upgrades to this app would be:

  - Transfer progress bar or information in percentages
  - Commands for moving files
  - Commands for changing directories
  - More efficient data transfer
  - SSL


***************************************************************

Note:

To test the app on local machine you can make 2 separate directories and
copy all content from "src" folder to both of them.
Then copy forbidden.txt (which is currently in project' root directory)
to the directory where the server will be running.
Now you can drop some files in to any of the directories you created
to test the file listing and transfer.

Files (1_test.pdf, 2_test.txt, 3_test.jpg) have been provided for this purpose.
Application has been tested in Windows and Linux enviroments.





