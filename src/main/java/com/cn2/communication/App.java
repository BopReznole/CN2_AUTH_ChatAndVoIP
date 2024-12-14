package com.cn2.communication;

import java.io.*;
import java.net.*;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import java.lang.Thread;

public class App extends Frame implements WindowListener, ActionListener {

	/*
	 * Definition of the app's fields
	 */
	static TextField inputTextField;		
	static JTextArea textArea;				 
	static JFrame frame;					
	static JButton sendButton;			
	static JTextField meesageTextField;		  
	public static Color gray;				
	final static String newline="\n";		
	static JButton callButton;				
	
	// Chat-related 
	private DatagramSocket datagramSocket; // declare datagramSocket  
	private InetAddress remoteAddress; // declare IP address remoteAddress, to set it as IP of remote 
	private ComChat comChat; // declare ComChat object for Chat 

	// VoIP-related 
//	private AudioRecord record; // declare object for recording sound  
//	private AudioPlayback playback; // declare object for playing sound  
	private ComVoIP comVoIP; // declare ComChat object for Chat 
	private volatile boolean isCalling = false; // VoIP call not happening

	/**
	 * Construct the app's frame and initialize important parameters
	 */
	public App(String title) {
		
		/*
		 * 1. Defining the components of the GUI
		 */
		
		// Setting up the characteristics of the frame
		super(title);								
		gray = new Color(254, 254, 254);		
		setBackground(gray);
		setLayout(new FlowLayout());			
		addWindowListener(this);	
		
		// Setting up the TextField and the TextArea
		inputTextField = new TextField();
		inputTextField.setColumns(20);
		
		// Setting up the TextArea.
		textArea = new JTextArea(10,40);			
		textArea.setLineWrap(true);				
		textArea.setEditable(false);			
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//Setting up the buttons
		sendButton = new JButton("Send");			
		callButton = new JButton("Call");			
						
		/*
		 * 2. Adding the components to the GUI
		 */
		add(scrollPane);							
		add(inputTextField);
		add(sendButton);
		add(callButton);
		
		/*
		 * 3. Linking the buttons to the ActionListener
		 */
		sendButton.addActionListener(this);			
		callButton.addActionListener(this);	
		
		/*
		 * 4. Initializing network components
		 */
		try {
			// chat-related components
			datagramSocket = new DatagramSocket(1234); // define datagramSocket and port=1234 
			remoteAddress = InetAddress.getByName("localhost"); // define to inetAddress the IP of remote 
			comChat = new ComChat(datagramSocket, remoteAddress); // pass datagramSocket, remoteAddress to to constructor ComChat 

			// VoIP-related components
//			record = new AudioRecord(); // initialize AudioRecord object
//			playback = new AudioPlayback(); // initialize AudioPlayback object
			comVoIP = new ComVoIP(datagramSocket, remoteAddress); // pass datagramSocket, remoteAddress to to constructor ComVoIP 
		}
		catch (Exception e) { // in case of error
			e.printStackTrace();
		}
	}
	
	/**
	 * The main method of the application. It continuously listens for
	 * new messages.
	 */
	public static void main(String[] args){
	
		/*
		 * 1. Create the app's window
		 */
		App app = new App("CN2 - AUTH");
		app.setSize(500,250);				  
		app.setVisible(true);			  

		/*
		 * 2. Start receiving Chat messages
		 */
		try {
			do { // local always waiting to receive data, infinite loop 
				app.comChat.receive(textArea);  /* call method receive from ComChat, receive text data */
			}while(true);
		}
		catch (Exception e) { // in case of error
			e.printStackTrace();
		}
	}
	
	/**
	 * The method that corresponds to the Action Listener. Whenever an action is performed
	 * (i.e., one of the buttons is clicked) this method is executed. 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		/*
		 * Check which button was clicked.
		 */
		
		if (e.getSource() == sendButton){ // The "Send" button was clicked
			
			String messageToSend  = inputTextField.getText(); // get string messageToSend from TextField inputTextField 
			if (!messageToSend.isEmpty()) { // if there is a messageToSend 
				try {
					comChat.send(messageToSend ); /* call method send from ComChat, send text data and start communication */
					textArea.append("local: " + messageToSend  + newline); // appear messageToSend to textArea and change line
					inputTextField.setText(""); // erase messageTosend from inputTextField 
				}
				catch (Exception ex) { // in case of error
					ex.printStackTrace();
				}
			}
		} 
		
		else if (e.getSource() == callButton){ // The "Call" button was clicked
			
			if (!isCalling) { // VoIP call happening 
				try {
					String message = ("Calling..."); // inform remote local is calling
					comChat.send(message); // by sending message
				} catch (Exception ex) { // in case of error
					ex.printStackTrace();
				}
				
				comVoIP.startVoIP(); // call method startVoIP and start VoIP call
				callButton.setText("End Call"); // change button to End Call
			} 
			else { // VoIP call not happening
				comVoIP.stopVoIP(); // call method stopVoIP and stop VoIP call
				callButton.setText("Call"); // change button to Call
			}
		}
	}

//	private void startVoIP() { // method to start VoIP call 
//		isCalling = true; // set isCalling to true, VoIP call happening
//		try {
//			record.open(); /* call method open from AudioRecord, open the targetLine-stream */
//			playback.open(); // call method open from AudioPlayback, open the sourceLine-stream */
//
//			// Thread to capture and send audio
//			new Thread(() -> {
//				try {
//					while (isCalling) {
//						byte[] audioData = record.read(); // audioData captures audio and returns byte stream 
//						DatagramPacket datagramPacket = new DatagramPacket(audioData, audioData.length, remoteAddress, 1243); 
//						// get all data from buffer, create a datagramPacket, send to IP remoteAddress and port of remote 
//						datagramSocket.send(datagramPacket); // datagramPacket send 
//					}
//				} 
//				catch (Exception e) { // in case of error
//					e.printStackTrace();
//				}
//			}).start(); // start Thread
//
//			// Thread to receive and play audio
//			new Thread(() -> {
//				try {
//					byte[] buffer = new byte[1024]; // buffer, size=1024 bytes, captures audio and returns byte stream 
//					while (isCalling) {
//						DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length); /* get packet datagramPacket to buffer */
//						datagramSocket.receive(datagramPacket); /* datagramPacket received from datagramSocket, blocking method */
//						playback.write(datagramPacket.getData()); /* call method write from AudioPlayback, play the audio */
//					}
//				} 
//				catch (Exception e) { // in case of error
//					e.printStackTrace();
//				}
//			}).start(); // start Thread
//
//		} 
//		catch (Exception e) { // in case of error
//			e.printStackTrace();
//		}
//	}
//
//	private void stopVoIP() { // method to stop VoIP call 
//		isCalling = false; // set isCalling to false, VoIP call not happening
//		record.stop();  // call method stop from AudioRecord, close targetLine-stream 
//		playback.stop();  // call method stop from AudioPlayback, close sourceLine-stream 
//	}

	/**
	 * These methods have to do with the GUI. You can use them if you wish to define
	 * what the program should do in specific scenarios (e.g., when closing the 
	 * window).
	 */
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		dispose();
        System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub	
	}
}




