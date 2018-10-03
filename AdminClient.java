

import java.io.*; //It gets the libraries for Input Output stream to be used by the socket
import java.net.*; // this gets the java network libraries which are used to run our servers
import java.util.*;

public class AdminClient{
 
 public static void main (String args[]) {
 String serverName;
 Socket sock;
 
 int port=Integer.parseInt(args[0]);

 serverName = "127.0.0.1";     //Allocating IP Adress to the ServerName if the args[] in the main is empty

 System.out.println("Chakradhar's AdminClient, 1.8.\n");
 System.out.println("Using server: " + serverName + ", Port: "+port);
 BufferedReader in = new BufferedReader(new InputStreamReader(System.in));  // Assigning in(variable) to the System.in(i/p stram) so that the User input will be read by using BufferedReader functions.
 try {                            //Start of try-catch Block
 String input;                                                               
 do {
 System.out.print
 ("Press J for Joke and P for Proverb to change the mode or quit to end: "); 
 System.out.flush ();                            //Removes the buffered content in the o/p stream
 input = in.readLine ();                            // Reading the user input 
 if (input.indexOf("quit") < 0)                     //Checks whether the input is quit if not then the condition will be executed 
{
	if((input.equals("J") || input.equals("P"))==false)    //checks whether the input is neither J nor P 
	{
     System.out.println("Invalid option please see the options and enter valid input");
	}
	else           //if the input  is either J or P then this condition is executed
	{
 
   sock = new Socket(serverName, port);           //socket is created at the client with the IP address and port number as we passed those as parameters.

   PrintStream toServer = new PrintStream(sock.getOutputStream());  // the o/p stream of the sock is connected to the i/p stream of the socket(server) at the other end.
   toServer.println(input); //Sends the input to Server to request mode change
    }
}
 } while (input.indexOf("quit") < 0);  //checking the condition if name has "quit" then indexof() will return -1 then the loop will be stopped.
 System.out.println ("Cancelled by user request.");
 } catch (IOException x) {x.printStackTrace ();}       //End of try-catch block, Catches if any i/o exceptions raised so taht the program won't be crashed.
 }
}