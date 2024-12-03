package com.cn2.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JTextArea;

public class ComChat {
	
	 private InetAddress remoteAddress; /* declare IP address remoteAddress, to set it as IP of remote */
	 private DatagramPacket datagramPacket; /* declare datagramPacket the datagramPacket sent or recieved */
	 private DatagramSocket datagramSocket; /* declare socket datagramSocket */
	 private byte[] buffer = new byte[1024]; /* declare buffer to store messages, size = 1024 bytes */

	 public ComChat(DatagramSocket datagramSocket, InetAddress remoteAddress) throws LineUnavailableException {
	 /* conctructor ComChat, initialize datagramSocket, remoteAddress */
		 
		 this.remoteAddress  = remoteAddress;
	     this.datagramSocket = datagramSocket;
	 }
	 
	 public void send(String messageToRemote) throws LineUnavailableException { /* method, local sends text messageToRemote to start communication */
	    	Thread sendTextThread = new Thread() { /* thread the send text process  */
	    		@Override public void run() { /* Override because child class run has same methods as parent class send */
	    			try { 
	    				buffer = messageToRemote.getBytes(); /* convert messageToRemote to bytes and put to buffer */
	    				DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, remoteAddress, 1243);
	    				/* get all data from buffer, create a datagramPacket, send to IP remoteAddress and port of remote */
	    				datagramSocket.send(datagramPacket); /* send datagramPacket */
	    			}
	    			catch (IOException e) { /* in case of error */
	    				e.printStackTrace();
	    			}
	    				
	    		}
	        };
	        sendTextThread.start(); /* start thread */
	    }
	    
	    public void receiveThenSend(JTextArea textArea) throws LineUnavailableException { /* method, local receives text first and then sends back */
	    	Thread receiveThenSendTextThread = new Thread() { /* thread the receiveThenSend text process  */
	    		@Override public void run() { /* Override because child class run has same methods as parent class receiveThenSend */
	    			try { /* receive */
	    				DatagramPacket datagramPacket= new DatagramPacket(buffer, buffer.length); /* get packet datagramPacket to buffer */
	    				datagramSocket.receive(datagramPacket); /* datagramPacket received from datagramSocket, blocking method */	
	    				InetAddress remoteAddress = datagramPacket.getAddress(); /* get IP address remoteAddress */
	    				int port = datagramPacket.getPort(); /* get port */
	    				String messageFromRemote = new String(datagramPacket.getData(), 0, datagramPacket.getLength()); 
	    				/* create string from datagramPacket byte array by remote, offset=0 */
	    				textArea.append("remote: " + messageFromRemote); /* appear messageFromRemote to textArea */
	    				textArea.append("\n"); /* change line */
	    				
	    		        /* send */
	    				datagramPacket = new DatagramPacket(buffer, buffer.length, remoteAddress, port); /* reassign datagramPacket to */
	    				datagramSocket.send(datagramPacket); /* send it to the remoteAddress and port of the remote */
	    			}
	    			catch (IOException e) { /* in case of error */
	    				e.printStackTrace();
	    			}
	    	    	
	    	    }
	    	};
	    	receiveThenSendTextThread.start(); /* start thread */
	    }
	
}
