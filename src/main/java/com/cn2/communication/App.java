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
	
	// define variables 
	private InetAddress remoteAddress; // define IP address remoteAddress, to set it as IP of remote 
	private UDPChat chatUDP; // define UDPChat object for UDP Chat 
	private VoIP voip; // define VoIP object for VoIP 
	private boolean isCallActive = false; // VoIP call not happening
	
//	private TCPChatReceiver ReceiverTCP;
//	private TCPChatSender SenderTCP;
	
	{ // initialize variables using non-static initialization block
	
	try {
		remoteAddress = InetAddress.getByName("localhost"); // initialize to remoteAddress the IP of remote 
		chatUDP = new UDPChat(new DatagramSocket(1234), remoteAddress); /* initialize chatUDP, pass DatagramSocket from port 1234 and
		remoteAddress to constructor UDPChat */ 
		voip = new VoIP(new DatagramSocket(1243), remoteAddress); /* initialize voip, pass DatagramSocket from port 1243 and
		remoteAddress to constructor VoIP */ 
		
//		ReceiverTCP = new TCPChatReceiver(new ServerSocket(5678));
//		SenderTCP = new TCPChatSender(new Socket(remoteAddress, 5678));			
	}
	catch (Exception e) { // in case of error
		e.printStackTrace();
		System.exit(1);
	}
	
	}

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
				app.chatUDP.receive(textArea);  // call method receive from chatUDP, receive text data 
//				app.ReceiverTCP.receive(textArea);
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
					chatUDP.send(messageToSend); // call method send from chatUDP, send text data
//					SenderTCP.send(messageToSend);
					textArea.append("local: " + messageToSend  + newline); // appear messageToSend to textArea and change line
					inputTextField.setText(""); // erase messageTosend from inputTextField 
				}
				catch (Exception ex) { // in case of error
					ex.printStackTrace();
				}
			}
		} 
		
		else if (e.getSource() == callButton){ // The "Call" button was clicked
			
			String textAreaText = textArea.getText(); // get the text from textArea
			if (!isCallActive) { // VoIP call happening 
				if (textAreaText.contains("remote: Calling...Pick up!")) { // if remote starts call
					callButton.setText("Acceptt Call"); // change button to Accept Call
					try {
						String message = ("Call picked up."); // inform remote local has picked up
						chatUDP.send(message); // by sending message
					} catch (Exception ex) { // in case of error
						ex.printStackTrace();
					}
					callButton.setText("End Call"); // change button to End Call
					voip.startVoIP(); // call method startVoIP from VoIP and start VoIP call
					isCallActive = true;
				}
				else { // if local starts call
					try {
						String message = ("Calling...Pick up!"); // inform remote local is calling
						chatUDP.send(message); // by sending message
					} catch (Exception ex) { // in case of error
						ex.printStackTrace();
					}
					textArea.append("Calling..." + newline); // appear "Calling..." to textArea
					callButton.setText("End Call"); // change button to End Call
					voip.startVoIP(); // call method startVoIP from VoIP and start VoIP call
					isCallActive = true;
				}
				
				if (textAreaText.contains("remote: Call picked up.") || textAreaText.contains("Call picked up.")) {
					// if local called and remote picked up and vice versa
					textArea.append("Call started" + newline); // appear "Call started." to textArea
				}
			} 
			else { // VoIP call not happening
				if (textAreaText.contains("Call ended.")) { // if remote ended call
					callButton.setText("Call"); // change button to Call
					voip.stopVoIP(); // call method stopVoIP from VoIP and stop VoIP call
					isCallActive = false;
				} 
				else { // if local ended call
					try {
						String message = ("Call ended."); // inform remote local has stopped the call
						chatUDP.send(message); // by sending message
					} catch (Exception ex) { // in case of error
						ex.printStackTrace();
					}
					callButton.setText("Call"); // change button to Call
					textArea.append("Call ended." + newline); // appear "Call ended." to textArea and change line
					voip.stopVoIP(); // call method stopVoIP from VoIP and stop VoIP call
					isCallActive = false;
				}
			}
		}
	}

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




