/* EE422C Final Project submission by
 * <Connie Wang>
 * <cw39276>
 * <76000>
 * Summer 2021
 */


package serverside;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Auction {
	private static final Object task = new Object();

    public ArrayList<Item> inventory = new ArrayList<Item>();

	public Auction(String filename) {
		try {
			Scanner kb = new Scanner(new File(filename));
			String line;
			String [] info;
			while(kb.hasNext()) {
				line = kb.nextLine();
				info = line.split(",");
				Item next = new Item(info);
				//next.print();
				inventory.add(next);
			}
			
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
       
    }
	
	public void sold(Item i) {
		inventory.remove(i);
	}

	
	
	public int bid(ClientThread c, Item i,  double amount) {
		synchronized(task) {
			if(i.open ==0) {
				System.out.println("invalid bid");
				return 2;
				}
			if(amount > i.price) {
				if(amount >= i.buynow) {
					i.open = 0;
				}
				i.setbidprice(amount, c);
				return 0;
				
			}
			else { System.out.println("invalid bid");}
			return 1;
		}
		
	}
	
	
	
}
