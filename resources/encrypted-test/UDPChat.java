package com.cn2.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;	//for utf test

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JTextArea;

public class UDPChat { // class for chat using UDP
	
	 private InetAddress remoteAddress; // define IP address remoteAddress, to set it as IP of remote 
	 private DatagramSocket datagramSocket; // define DatagramSocket datagramSocket 
	 private byte[] sendBuffer = new byte[1024]; // define buffer to store messages, size = 1024 byte    
	 private byte[] receiveBuffer = new byte[1024];
	 public UDPChat(DatagramSocket datagramSocket, InetAddress remoteAddress) throws LineUnavailableException {
	 // conctructor UDPChat, initialize datagramSocket, remoteAddress 
		 
		 this.remoteAddress  = remoteAddress;
	     this.datagramSocket = datagramSocket;     
	 }
	 
	 public void send(String messageToRemote) throws LineUnavailableException { // method send, local sends text messageToRemote
	 	 
	 	 try {
	 		System.err.println(messageToRemote.length());
	 		System.err.println(messageToRemote.getBytes().length);
	 		sendBuffer = messageToRemote.getBytes(); // convert messageToRemote to bytes and put to buffer
	 		System.err.println(sendBuffer.length);
	 		
	 		 DatagramPacket datagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length, remoteAddress, 1234); /* construct datagramPacket,  
			 send packets of length of buffer, to IP inetAddress and port=1234 of remote */  
	 		System.err.println(datagramPacket.getLength());
	 		 datagramSocket.send(datagramPacket); // send datagramPacket
	 		
	 	 }
	 	 catch (IOException e) { // in case of error
	 		 e.printStackTrace();
	 	 }
	 }
	 
	 public void receive(JTextArea textArea, AESci aesci) throws LineUnavailableException { // method receive, local receives text messageFromRemote
	 	 
	 	 new Thread(() -> { // Thread the receive text process
	 	 	 while (true) { // local always waiting to receive data, infinite loop
	 	 	 	 try {
	 	 	 		 DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length); /* construct datagramPacket,
     	 	 		 receive packets of length of buffer */ 
     	 	 		 datagramSocket.receive(datagramPacket); // datagramPacket received from datagramSocket, blocking method  
     	 	 		 String messageFromRemote = new String(datagramPacket.getData(), 0, datagramPacket.getLength()); 
     	 	 		 // create string from datagramPacket byte array by remote, offset=0
//     	 	 		 aesci.exportIV();
     	 	 		System.err.println(datagramPacket.getLength());
     	 	 		 messageFromRemote = aesci.decryptMessage(messageFromRemote);
     	 	 		 
     	 	 		 textArea.append("remote: " + messageFromRemote + "\n"); // appear messageFromRemote to textArea and change line
	 	 	 	 }
	     	 	 catch (IOException e) { // in case of error
	     	 		 e.printStackTrace();
	     	 	 } 
	 	 	 	 catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	//maybe unecessary
	 	 	 }
	 	 }).start(); // start Thread
	 }
//	 public void receive(JTextArea textArea, AESci aesci) throws LineUnavailableException {
//		    new Thread(() -> {
//		        while (true) {
//		            try {
//		                DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
//		                datagramSocket.receive(datagramPacket);
//		                int length = datagramPacket.getLength();
//		                byte[] messageBytes = new byte[length];
//		                System.arraycopy(datagramPacket.getData(), 0, messageBytes, 0, length);
//		                System.err.println(length);
//		                String messageFromRemote = aesci.decryptMessage(new String(messageBytes, 0, length, StandardCharsets.UTF_8));
//		                textArea.append("remote: " + messageFromRemote + "\n");
//		            } catch (IOException e) {
//		                e.printStackTrace();
//		            } catch (Exception e) {
//		                e.printStackTrace();
//		            }
//		        }
//		    }).start();
//		}


}
