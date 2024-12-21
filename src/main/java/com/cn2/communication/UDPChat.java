package com.cn2.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JTextArea;

public class UDPChat {
	
	 private InetAddress remoteAddress; // define IP address remoteAddress, to set it as IP of remote 
	 private DatagramSocket datagramSocket; // define DatagramSocket datagramSocket 
	 private byte[] buffer = new byte[1024]; // define buffer to store messages, size = 1024 byte    

	 public UDPChat(DatagramSocket datagramSocket, InetAddress remoteAddress) throws LineUnavailableException {
	 // conctructor UDPChat, initialize datagramSocket, remoteAddress 
		 
		 this.remoteAddress  = remoteAddress;
	     this.datagramSocket = datagramSocket;     
	 }
	 
	 public void send(String messageToRemote) throws LineUnavailableException { // method, local sends text messageToRemote  
		 
		 try { 
				buffer = messageToRemote.getBytes(); // convert messageToRemote to bytes and put to sendBuffer 
				DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, remoteAddress, 1234);
				// get all data from buffer, create a datagramPacket, send to IP remoteAddress and port of remote 
				datagramSocket.send(datagramPacket); // send datagramPacket 
			}
			catch (IOException e) { // in case of error 
				e.printStackTrace();
			}	
		}
	    
	    public void receive(JTextArea textArea) throws LineUnavailableException { // method, local receives text messageFromRemote 
	    	
	    	new Thread(() -> { // Thread the receive text process
	    		while (true) { // local always waiting to receive data, infinite loop
	    			try	{ 
	    				DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length); // get packet datagramPacket to receiveBuffer 
	    				datagramSocket.receive(datagramPacket); // datagramPacket received from datagramSocket, blocking method  
	    				String messageFromRemote = new String(datagramPacket.getData(), 0, datagramPacket.getLength()); 
	    				// create string from datagramPacket byte array by remote, offset=0 
	    				textArea.append("remote: " + messageFromRemote + "\n"); // appear messageFromRemote to textArea and change line
	    			}
	    			catch (IOException e) { // in case of error  
	    				e.printStackTrace();
	    			}
	    		}
	    	}).start(); // start Thread	
	    }
	    
}
