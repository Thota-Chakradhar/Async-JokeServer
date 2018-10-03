
	import java.io.*; // Imports the Input Output libraries
	import java.net.*; // Imports the Java networking libraries
  import java.util.ArrayList;
	import java.util.*;

    class AdminWorker extends Thread
{
     Socket sock;  //Used for storing the Socket which we got as a parameter in C'tor.
     
     AdminWorker (Socket s)  //C'tor initializes the args to the appropriate local variables
     {
         sock = s;
     }
    
    public void run()        //The func which Thread will execute when ever it starts
    {
         PrintStream out = null; //These variables are used for the communication b?w client and Server
     BufferedReader in = null;
     String userInput=null;
     try
     {
     	in=new BufferedReader(new InputStreamReader(sock.getInputStream()));     // the i/p stream of the sock is connected to the o/p stream of the socket(server) at the other end.
     out= new PrintStream(sock.getOutputStream());
     
     userInput=in.readLine();                //Receives the input from AdminClient
    
     synchronized(this)                     //This section of code is blocked and only one process has the control to access at a time.
     {
     if(AsyncJokeServer.mode.equals("Joke") && userInput.equals("P"))  //Determines whether to change the mode or not
     {
     changemode();
     }
     else if(AsyncJokeServer.mode.equals("Proverb") && userInput.equals("J"))
     {
     changemode();
     }
     else{}
      }
     }catch(IOException x)  //If any exceptions raised will be handled here
	 {
	 x.printStackTrace ();
	 }


    
   }
  public synchronized void changemode()     //This function is synchronized so at max only one process has the control to access.

  {
     if(AsyncJokeServer.mode=="Joke")     //Changes the Mode of Server 
     {
      AsyncJokeServer.mode="Proverb";
     }

     else if(AsyncJokeServer.mode=="Proverb")
     {
       AsyncJokeServer.mode="Joke";  
     }
  }

}


 class AdminLooper implements Runnable {      //AdminLooper is used to handle the AdminClients 

    public static boolean adminControlSwitch = true;
    private int port ;  //the number at whch the client and server has to be connected.

    AdminLooper(int port)
    {
      this.port=port;
    }

    public void run() 
          {  

        int q_len = 6; 
        
        Socket sock;

        try {
            ServerSocket servsock = new ServerSocket(port, q_len); //creates a server socket bound to the portnumber passed as a parameter to the C'tor.
    
            while (adminControlSwitch) {    // waits for the next ADMIN client connection:
               sock = servsock.accept();    // waits for the client to connect on the specified port.
               
                new AdminWorker(sock).start();  // starts a worker thread to handle the client requests.

            }    //while loop continues for next clients.

        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}


    class ServerWorker extends Thread
	{
		public ArrayList<String> Proverbs=new ArrayList<String>();    //Proverbs and Jokes list have all proverbs and jokes to sent to client
		public ArrayList<String> Jokes=new ArrayList<String>();
		private int sleepTime;   
		private int UDPport;

	 Socket sock; // Class member, socket, local to Worker.
	 ServerWorker (Socket s,int sleepTime)   //C'tor initializes the sleeping time,Socket and adds the JOkes and Proverbs msgs
	 {
		 sock = s;
		 this.sleepTime=sleepTime;
		Proverbs.add("Proverb A: work hard and get success");
		Proverbs.add("Proverb B: Money is not the comfort");
		Proverbs.add("Proverb C: Failure is the first step of success");
		Proverbs.add("Proverb D: Dont run after money, run for your Happiness");
		Jokes.add("Joke A: Dont except jokes from me");
		Jokes.add("Joke B: Listen to my jokes at your own risk");
		Jokes.add("Joke C: I am not interested to play any jokes");
		Jokes.add("Joke D: Joker is not the best humour person");

	 } 

	 public void run()    //The func which Thread will execute when ever it starts
	 {
    
	 PrintStream out = null;
	 ObjectInputStream in = null;
	 ArrayList<String> SentJokes=null;
    ArrayList<String> SentProverbs=null;
	 try {                                       //start of outer try-catch block.
	 in =new ObjectInputStream(sock.getInputStream());       // the i/p stream of the sock is connected to the o/p stream of the client.
	 out = new PrintStream(sock.getOutputStream());       // the o/p stream of the sock is connected to the i/p stream of the client.
   
	 try {                        //start of inner try-catch block

		 
	 SentJokes=new ArrayList<String>((ArrayList<String>)in.readObject ());      //Receives the Jokes annd Proverbs Sentlist from the Client 
 	 SentProverbs=new ArrayList<String>((ArrayList<String>)in.readObject ()); 
   UDPport=Integer.parseInt((String)in.readObject());

   }                //End of inner try-catch block.
    catch (IOException x)          
	 {                           
	 System.out.println("Server read error");            // if any exceptions raised this msg will be printed.
	 x.printStackTrace ();
	 }
	 catch(ClassNotFoundException x)
	 {
	 x.printStackTrace ();
	 }
	 sock.close();                                      //breaks the connection with the client;
	 } catch (IOException ioe) {System.out.println(ioe);}  //end of outer try-catch block.
  
  try{
	 Thread.sleep(sleepTime);                          //Process Sleeps for some determined time
	 }catch(InterruptedException e){e.printStackTrace();}
     if(AsyncJokeServer.mode.equals("Joke"))          //Checking the Server mode and the appropriate Msg will be sent by calling Sendmessage func
	 {
	 	ArrayList<String> temp=new ArrayList<String>(Jokes);
	 	Sendmessage(temp,SentJokes,UDPport);
	 }
	 else
	 {
	 	ArrayList<String> temp=new ArrayList<String>(Proverbs);
	 	Sendmessage(temp,SentProverbs,UDPport);	 
	 }

}


 public void Sendmessage (ArrayList<String> temp,ArrayList<String> Sentlist, int UDPport)
	 {

	 	if(Sentlist.size()>0 && Sentlist.size()!=4)        //Checking whether Sentlist is not empty and all msgs not sent
	 	{
	 		for(int i=0;i<Sentlist.size();i++)               //if it is true then all sent msgs will be removed from temp 
	 		{
	 			temp.remove(Sentlist.get(i));
	 		}
	 	}
	 
	 	int tempSize=temp.size();                     //length of temp is stored
	 
       Random randomno = new Random();
       int rand=randomno.nextInt(tempSize);      //Gets the random num which is used to select the random Joke or proverb
       String s=temp.get(rand);

       try
		{
      
		DatagramSocket udp = new DatagramSocket();           //Using UDP the worker will connect back to the client 
        byte[] Datatosend = s.getBytes();
    
       try{
       InetAddress ip=InetAddress.getByName("localhost");
             
           DatagramPacket PackettoSend = new DatagramPacket(Datatosend, Datatosend.length,ip,UDPport);  
   
	     udp.send(PackettoSend);        //Sends the random msg to the appropriate client using the UDPport 
	      }catch(IOException e){e.printStackTrace();}
	   }                       
	catch(SocketException e){e.printStackTrace();}
	

	}
	
	}


	public class AsyncJokeServer {

    

		public static String mode="Joke";            //mode of the Server.Initially it is in Joke mode
 
	 public static void startServers(int port,int sleepTime) throws IOException
   {

AdminLooper AL = new AdminLooper(port+100); // Admin Looper for Admin Clients
   Thread t = new Thread(AL);          //creating Admin Looper Thread and starts
   t.start();

 int q_len = 6; /* Not intersting. Number of requests for OpSys to queue */
                
 Socket sock;

 ServerSocket servsock = new ServerSocket(port, q_len);  //creates a server socket bound to the portnumber passed as a parameter to the C'tor.

 System.out.println
 ("Chakradhar's Asynchronous JokeServer listening at port "+port+".\n");
 while (true) {
 sock = servsock.accept(); // waits for the client to connect on the specified port.
 new ServerWorker(sock,sleepTime).start();


  }
   }

   public static void main(String args[]) throws IOException {
		 
     int port=Integer.parseInt(args[0]);              //the number at whch the client and server has to be connected.  
     int sleepTime=70000;

startServers(port,sleepTime);

}
}
