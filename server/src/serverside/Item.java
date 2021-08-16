/* EE422C Final Project submission by
 * <Connie Wang>
 * <cw39276>
 * <76000>
 * Summer 2021
 */

package serverside;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class Item implements Serializable{
	
	public String name;
	public String description;
	public double price;
	public double buynow;
	public String buyer;
	public int open;
	public int bidtime;
	//public BufferedImage img = null;
	
	
	
	public Item(String [] info) {

			this.name = info[0];
			this.price = Double.parseDouble(info[1]);
			this.buynow =  Double.parseDouble(info[2]);
			this.description = info[3];
			bidtime = Integer.parseInt(info[4]);
			open  = 1;
		
	}
	
	
	public void setbidprice(double p, ClientThread c){
        price = p;
        buyer = c.name;
    }
	
	@Override
	public String toString() {
		
		String s =  open +"," +name +"," + price +","
			+ buyer + "," + buynow + ","+ description + "," +bidtime;
		return s;
	}
	
	public String print() {
		String s = "Item: " + name +"\nCurrent Bid " + price +"\nHighest Bidder "
			+ buyer + "\nBuyNow : " + buynow + "\ndesc: " + description;
		return s;
	}
	
	
}
/*		if(info.length == 0) {
return;
}
if(info.length ==1) {
this.name = info[0];
this.price = 1.00;
}
if(info.length == 2) {
this.name = info[0];
this.price = Double.parseDouble(info[1]);
return;
}
if(info.length == 3) {
this.name = info[0];
this.price = Double.parseDouble(info[1]);
this.buynow =  Double.parseDouble(info[2]);
return;
}
else {*/