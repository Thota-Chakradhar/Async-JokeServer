import java.io.*; // Imports the Input Output libraries
import java.net.*; // Imports the Java networking libraries
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

class UDPServer extends Thread
{                                               //Local variables will be initialized in the C'tor

	AsyncJokeClient client;           
    int port;
    String serverName;
    String JokeServer;
    ArrayList<String> Proverbs;
    ArrayList<String> Jokes;
	
	//C'tor initializes all parameters to the appropriate variables
	UDPServer(AsyncJokeClient client,int port,String serverName,String JokeServer,ArrayList<String> Proverbs,ArrayList<String> Jokes)
	{
       this.client=client;
	   this.port=port;
	   this.serverName=serverName;
	   this.JokeServer=JokeServer;
	   this.Proverbs=Proverbs;
	   this.Jokes=Jokes;
	}


	public void run()         //The func which Thread will execute when ever it starts

	{
		String textFromServer=null;        //Used to store the Msg from Server
		try
		{

		DatagramSocket serverSocket = new DatagramSocket(port);        //starts the UDPserver which will communicate at the specified port and receives the Msg 
        byte[] receiveData = new byte[100];                   //Determines the Max length of Msg the sever has to receive
            
         try{
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);   
         serverSocket.receive(receivePacket);            //Receives the Msg in Datagram from Server
         textFromServer = new String( receivePacket.getData());   //Converts into string
         }catch(IOException e){e.printStackTrace();}
    	
    	 if (textFromServer != null)    		 // If the message is not null(empty) then the messages will be displayed.

     {
	     if(textFromServer.contains("Joke"))            //Checking whether the Msg is Joke or proverb and adds th Msg to the list
	     {
	     	if(Jokes.size()==4)                        //If the client previously receives all the Messages from the client then the list will be cleared
	     	{
	     		Jokes.clear();
	    	}
	    		 Jokes.add(textFromServer.trim());    //Adds the Msg to the list after trim() which eliminates the trailing whitespaces
	    
	     }
	     else
	     {
	     	if(Proverbs.size()==4)
	     	{
	     		Proverbs.clear();
	    	}
	    	 Proverbs.add(textFromServer.trim());
	     } 

	     while(client.sumStatus==1){}        //checks the sumstatus and loops until the addition is done
         System.out.println();
	     System.out.println("Server "+JokeServer+" responds: "+textFromServer);
	}
       }catch(SocketException e){e.printStackTrace();}
    }

 


}

public class AsyncJokeClient {

public static int UDPport=5000;            //port number which will be used for UDP

public int sumStatus=0;          //Used to determine whether the sum of numbers is still processing or not

public  static void getJokeorProverb (AsyncJokeClient client,int port,String serverName,String JokeServer,ArrayList<String> Proverbs,ArrayList<String> Jokes)
{      //Function is used send the Jokes and Proverbs list to the server to get new msg


	 Socket sock;
	 BufferedReader fromServer;
	 ObjectOutputStream toServer;
	 String textFromServer;                                              //This string is used to store the content(messages) received from the server.

	 try{                                                      //start of try-catch block
	 sock = new Socket(serverName, port);                      //socket is created at the client with the IP address and port number as we passed those as parameters.

	 fromServer =
	 new BufferedReader(new InputStreamReader(sock.getInputStream()));     // the i/p stream of the sock is connected to the o/p stream of the socket(server) at the other end.
	 toServer = new ObjectOutputStream(sock.getOutputStream());                   // the o/p stream of the sock is connected to the i/p stream of the socket(server) at the other end.

	                                                                  //the above two connections are used to establish the communication b/w server and client.

	 toServer.writeObject(Jokes);               //Sends the Jokes and Proverbs list to the Server
	 toServer.writeObject(Proverbs);
      client.UDPport++;
	 int portUDP=client.UDPport;
	 toServer.writeObject(String.valueOf(portUDP));    //Sends the Udp portnum to Client
   
	  toServer.flush();

     UDPServer s=new UDPServer(client,UDPport,serverName,JokeServer,Proverbs,Jokes);   //The UDPserever is started at the UDPport we got from Server
     s.start();
    
	 sock.close();                           //breaks the socket connection with the server.
	 } catch (IOException x) {
	 System.out.println ("Socket error.");
	 //end of try-catch block if any i/o exceptions raised this msg will be printed.
	 x.printStackTrace ();
	 }

	}



	public static void main (String args[])
	{         //Arraylists are used to store all received Jokes and Servers from different Servers

	   char letters='A';                //Used to know how many servers are started
       String temp="";
       String Serverslist="";           

		HashMap<String,ArrayList<String>> ServerMsgs=new HashMap<String,ArrayList<String>>();   //Stores all msgs got from all servers differently 
	  
	       HashMap<String,Integer> ServerType=new HashMap<String,Integer>(); //Stores the Server Name and port num
         

             ArrayList<String> MsgList=null;


              for(int i=0;i<args.length;i++)                  //For every iteration the ports ,server names and Jokes and proverbs list will be initialized and added to the particular hashmap
              {
              	
                 temp=String.valueOf(letters);            //Giving name to every port which we got as an arguments
                 ServerType.put(temp,Integer.parseInt(args[i]));
                 

                 Serverslist=Serverslist+temp+" or ";
                 
                 MsgList=new ArrayList<String>();

              	ServerMsgs.put(temp+"Jokes",MsgList);

                 MsgList=new ArrayList<String>();

              	ServerMsgs.put(temp+"Proverbs",MsgList);
               letters++;
              }
              
AsyncJokeClient client=new AsyncJokeClient();        //Client is created and will be used for checking the sum status
	
	 String serverName;
	 
	  serverName = "127.0.0.1";     //Allocating IP Adress to the ServerName if the args[] in the main is empty
	 

	 System.out.println("Asynchronous JokeClient started with bindings:");
     
     for (Map.Entry<String, Integer> t : ServerType.entrySet())    //Displays the info of all Servers which started
      {
    System.out.println("Server "+ t.getKey() +" at "+t.getValue());
 
      }
 
     BufferedReader in = new BufferedReader(new InputStreamReader(System.in));  // Assigning in(variable) to the System.in(i/p stram) so that the User input will be read by using BufferedReader functions.
	 try {                            //Start of try-catch Block
	 String input;
	 do {
    
	 System.out.print
	 ("Enter "+Serverslist+" to get a joke or proverb or numbers for sum: ");
     

	 System.out.flush ();                            //Removes the buffered content in the o/p stream
	 input = in.readLine ();                         // Reads the user input
    
     
	 if (input.indexOf("quit") < 0)
	 {                 
     if(input.length() <3)       //Checks the Input which determines from where we have to get the MSg and the func will be called by passing appropriate parameters
     {
     	int inputport=ServerType.get(input.trim());  //Determines the port num of the User input
     	
     	getJokeorProverb (client,inputport,serverName,input,ServerMsgs.get(input+"Proverbs"), ServerMsgs.get(input+"Jokes")); 
     }
     

     else                      //If the Input is two numbers then addition will be done
     {
        client.sumStatus=1;     //This is used to stop printing the Msg we got from server until the addition is done 
     
     	int a,b,result;
        int index=input.indexOf(' ');       
        a=Integer.parseInt(input.substring(0,index));         //Converting the String to integers        
       
        b=Integer.parseInt(input.substring(index+1,input.length()));
		result=a+b;
		System.out.println("Sum of two numbers are: "+result);	 //Prints the Sum
        
        client.sumStatus=0;      //Setting back to 0 so other processes will resume
     }
     
	 }
	 } while (input.indexOf("quit") < 0);   //loops back to accept more user inputs until the input is quit.
	 System.out.println ("Cancelled by user request.");
	 } catch (IOException x) {x.printStackTrace ();}       //End of try-catch block, Catches if any i/o exceptions raised so taht the program won't be crashed.
	 }

 
}
