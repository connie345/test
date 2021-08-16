/* EE422C Final Project submission by
 * <Connie Wang>
 * <cw39276>
 * <76000>
 * Summer 2021
 */

package serverside;

import java.io.*;
import java.net.*;
import java.util.*;



@SuppressWarnings("deprecation")
public class Server implements Observer{

 
	//private static List<Client> users;
	private int port;
	private static final Object task = new Object();
	public ArrayList<ClientThread> userThreads = new ArrayList<ClientThread>();
	public static HashMap <String , String> users = new HashMap <String,String>();
	public ArrayList<String> bids = new ArrayList<String>();
    public static Auction auction;
    public static int timeleft;
    public static int bidnum ;

    public static void main (String [] args) {
        Server server = new Server();
        auction = new Auction("./Items.txt");
       // System.out.print("test");
        users.put("Guest","null");
        users.put("A","a");
        users.put("B","b");
        users.put("C","c");
        users.put("D","d");
        users.put("E","e");
        users.put("F","f");
        users.put("Connie","c");
        bidnum = 0;
        timeleft = (5 * 60);
        server.SetupNetworking();
    }
    
    @Override
	public void update(Observable o, Object arg) {
    	for(ClientThread c: userThreads) {
    		c.sendone("System-a-"+arg);
    	}
		// TODO Auto-generated method stub
		
	}
    
    private void SetupNetworking() {
    	try  {
    		ServerSocket serverSocket = new ServerSocket(8001);
    		
    		
    		Timer timer = new Timer();
    	    timer.scheduleAtFixedRate(new TimerTask() {
    	        @Override
    	        public void run() {
    	        	System.out.println("timeleft: " + timeleft);
    	        	--timeleft;
    	        	for(Item i: auction.inventory) {
    	        		i.bidtime--;
    	        		if(i.bidtime == 0) {i.open = 0;}
    	        	}
    	        	if(timeleft == 0) {
    	        		synchronized(task) {
    	        		for(ClientThread c: userThreads) {
    	            		c.stop();
    	            	}}
    					try {
							serverSocket.close();
							System.exit(0);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					System.exit(0);
    	        	}
    	        }
    	    }, 1000, (1000));
    	    /*
    		try {
        		for(ClientThread c: userThreads) {
            		c.stop();
            	}
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    
    		*/
            while (true) {
            	System.out.println("waiting for new user");
                Socket user = serverSocket.accept();
                System.out.println("New user connected");
 
                ClientThread newUser = new ClientThread(user, this, auction);
                Thread t = new Thread(newUser);
                userThreads.add(newUser);
                
                t.start();
                System.out.println("d");
               // flag = 1;
 
            }
 
        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    	
    }


	
    
    
    /*

    private void SetupNetworking() {
    //	ServerSocket serverSocket = new ServerSocket(4001); 
	//	while (true) { 
	//		Socket clientSocket = serverSocket.accept();
		//	ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
	//		Thread clientThread = new Thread(new ClientHandler(clientSocket, writer)); 
		//	clientThread.start(); 
	//	}
      //  int port = 5000;
        try {
      //      ServerSocket ss = new ServerSocket(port);
      //      while (true) {
       //         Socket clientSocket = ss.accept();
          //      ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
          //      Thread t = new Thread(new ClientHandler(clientSocket, writer));
         //       t.start();
          //      addObserver(writer);
                System.out.println("got a connection");
       //     }
   //     } catch (IOException e) {}
    }

    class ClientHandler implements Runnable {
        private  ObjectInputStream reader;
        //private  ClientObserver writer; // See Canvas. Extends ObjectOutputStream, implements Observer
        Socket clientSocket;
/*
        public ClientHandler(Socket clientSocket, ClientObserver writer) {
			
        }*/
/*
        public void run() {
			
        }*/
  //  } // end of class ClientHandler
}