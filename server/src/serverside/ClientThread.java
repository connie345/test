/* EE422C Final Project submission by
 * <Connie Wang>
 * <cw39276>
 * <76000>
 * Summer 2021
 */

package serverside;

import java.io.*;
import java.net.*;
import java.security.Key;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

@SuppressWarnings("deprecation")
public class ClientThread extends Observable implements Runnable {
	public String name;
	private Socket socket;
	private Server server;
	private PrintWriter writer;
	private boolean exit = false;;
	String key = "Bar12345Bar12345";
	String salt = "salt";
	public Auction a;
	public static HashMap <String , String> users = new HashMap <String,String>();
	public OutputStream output;
	SecretKey AESKey;
	static String test = "testestes";
	private Cipher keyDecipher;
	private Cipher DecryptCipher;
	private Cipher EncryptCipher;
	public ObjectOutputStream objectOutputStream;
	public InputStream inputStream;
	public ObjectInputStream objectInputStream;

	public ClientThread(Socket socket, Server server, Auction a) {
		this.socket = socket;
		this.server = server;
		users.putAll(Server.users);
		addObserver(server);
		this.a = a;
		try {
			output = socket.getOutputStream();
			objectOutputStream = new ObjectOutputStream(output);
			inputStream = socket.getInputStream();
			objectInputStream = new ObjectInputStream(inputStream);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void checkUser(String pwd) throws IOException {
		System.out.println(name);
		if(users.containsKey(name)) {
			System.out.println("here");
			if(pwd.equals(Server.users.get(name))) {
				for(ClientThread c: server.userThreads) {
					if(name.equals(c.name)&& c!=this && !name.equals("Guest")) {
						sendone("System-Error-inuse");
						return;
					}
					
				}
				sendone("System-Error-ok");
				System.out.println("Sending inv");
				ArrayList<Item> i = a.inventory;
                sendInven(i);
                System.out.println("Sent inv");
                
                System.out.println("Sent welcome");
				return;
			}
		}
		sendone("System-Error-invalid");
		
		//server.userThreads.remove(this);
		//socket.close();
	}
	
	public void sendMessage(String s) {

		try {
			for(ClientThread c: server.userThreads) {
				c.objectOutputStream.writeObject(s);
				System.out.println("System-Ann-"+s);
			}
			// objectOutputStream.flush();
			// objectOutputStream.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			sendMessage(s);
		}
	}
	
	public void sendone(String s) {

		try {
				objectOutputStream.writeObject(s);
				System.out.println(s);
			
			// objectOutputStream.flush();
			// objectOutputStream.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			sendone(s);
		}
	}

	public void sendInven(ArrayList<Item> i) {
		try {
			ArrayList<Object>arr = new ArrayList<Object>();
			System.out.println("writing");
			for (Item item : i) {
				arr.add(item.toString());
			}
			arr.add(server.bids);
			System.out.println(arr);
			objectOutputStream.writeObject(arr);
			
			sendone("System-Ann- Welcome to the auction!");
            sendMessage("System-Ann-New user [" + name + "] has connected");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void updateItem(Item i) {

		try {
			System.out.println("update item");
			if(i.open == 0) {
				//sole
				server.bids.add(i.name+"-"+i.name + "sold to " + i.buyer + " for " + i.price);
			}
			else {
			server.bids.add(i.name+"-"+i.buyer + ": " + i.name + " for " + i.price);}
		//	System.out.println("ipdate item now bid:"+ i.bids);
			for(ClientThread c: server.userThreads) {
				System.out.print("a");
				c.objectOutputStream.writeObject("System-update-"+i.toString());
			}
			System.out.println(i.toString());
			Server.bidnum++;
			// objectOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			updateItem(i);
		}

	}

	// @Override
	public void start() {
		run();
	}
	
	 public void stop()
	    {
		 	sendone("zzzquit");
		 	sendMessage("System-Ann-User ["+name+"] has quit");
		 	server.userThreads.remove(this);
		 	try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	    }
	 
	 private String decrypt(byte[] s) {
		String asdf = null;
	        try
	        {
	        	Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
	        	 Cipher cipher = Cipher.getInstance("AES");
	        	cipher.init(Cipher.DECRYPT_MODE, aesKey);
	        	String decrypted = new String(cipher.doFinal(s));
	        	return decrypted;
	        	/*
	            DecryptCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	            DecryptCipher.init(Cipher.DECRYPT_MODE, AESKey, new IvParameterSpec(test.getBytes()));
	             byte[] msg = DecryptCipher.doFinal(s);		
	             String ret = new String(msg);
	             System.out.println("Server: INCOMING Message From CLIENT >> " + new String(msg));
	             System.out.println("Sever: Enter OUTGOING  message : > ");
	             return ret;*/
	        }
	        
	        catch(Exception e)
	         {
	        	//e.printStackTrace();
	         }
			return asdf;
	       
	        //	return asdf;
	    }

	// @Override
	@Override
	public void run() {
		
		try {

			while (true) {
				try {
					Object o = objectInputStream.readObject();
					String response = decrypt((byte[]) o);
					if (response instanceof java.lang.String) {
						System.out.println("\n" + response);
						System.out.print("[" + name + "]: " + response);
						String[] info = ((java.lang.String) response).split(",");
						System.out.println(info[0]);
						if (info[0].equals("NAME")) {
							name = info[1];
							String password = info[2];
							checkUser(password);
						}
						if (info[0].equals("cmdBid")) {
							System.out.println("recieve bid");
							String s = info[1];
							double d = Double.parseDouble(info[2]);
							Item i = null;
							for (Item item : a.inventory) {
								if (item.name.equals(s)) {
									i = item;
								}
							}
							int flag = a.bid(this, i, d);
							if(flag == 1) {sendone("System-Error-Invalid Bid");}
							else if(flag == 2) {sendone("System-Error-Item has already been sold");}
							else {
							updateItem(i);
							}
						}
						if (info[0].equals("cmdquit")) {
								stop();
								return;
							
						}
						
					}
				
					// objectInputStream.reset();
				} catch (IOException ex) {
					System.out.println("Error reading from server: " + ex.getMessage());
					ex.printStackTrace();
					break;
				}
			}
			//objectInputStream.close();
	
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
