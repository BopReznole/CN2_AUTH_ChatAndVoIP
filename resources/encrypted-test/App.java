package com.cn2.communication;

import java.io.*;
import java.net.*;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.sound.sampled.LineUnavailableException;
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
	
	// define network variables 
	private InetAddress remoteAddress; // define IP address remoteAddress, to set it as IP of remote 
	private UDPChat chatUDP; // define UDPChat object for UDP Chat 
	private VoIP voip; // define VoIP object for VoIP 
	private boolean isCallActive = false; // VoIP call not happening
	
//	private TCPChatClient chatTCP; // define TCPChat object for TCP Chat, if local is the "client"
	private TCPChatServer chatTCP; // define TCPChat object for TCP Chat, if local is the "server"
	
	// define aes variables
	private static AESci aesci;
	
	{ // initialize network variables using non-static initialization block
	
	try {
		remoteAddress = InetAddress.getByName("192.168.2.12"); // initialize remoteAddress, IP of remote
		chatUDP = new UDPChat(new DatagramSocket(1234), remoteAddress); /* initialize chatUDP,
		pass DatagramSocket from port 1234 and remoteAddress to constructor UDPChat */ 
		voip = new VoIP(new DatagramSocket(1243), remoteAddress); /* initialize voip,
		pass DatagramSocket from port 1243 and remoteAddress to constructor VoIP */
		
//		chatTCP = new TCPChatClient(new Socket("192.168.1.14", 2345)); /* initialize chatTCP,
//		pass Socket from port 2345 and IP of remote to constructor TCPChatSender */ 
//		chatTCP = new TCPChatServer(new ServerSocket(2345)); // initialize chatTCP, pass ServerSocket from port 2345 to constructor TCPChatReceiver 
	}
	catch (Exception e) { // in case of error
		e.printStackTrace();
		System.exit(1);
	}
	
	// initialize aes variable
	try {
		aesci = new AESci();
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
	 * @throws LineUnavailableException  
	 */
	public static void main(String[] args) throws LineUnavailableException {
		
		/*
		 * 1. Create the app's window
		 */
		App app = new App("CN2 - AUTH");
		app.setSize(500,250);				  
		app.setVisible(true);			  

		/*
		 * 2. Start receiving Chat messages
		 */
		app.chatUDP.receive(textArea, aesci);  // call method receive from chatUDP, receive text data

//		app.chatTCP.receive(textArea); // call method receive from TCPChatSender or TCPChatRceiver, receive text data

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
					String plainMessage = messageToSend; //stores the message in plaintext
					chatUDP.send(messageToSend, aesci); // call method send from chatUDP, send text data
//					chatTCP.send(messageToSend); // call method send from chatTCP, send text data
					textArea.append("local: " + plainMessage  + newline); // appear plainMessage to textArea and change line
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
					try {
						String message = ("VoIP call started."); // inform remote local has picked up, call started
						chatUDP.send(message, aesci); // by sending message
					} 
					catch (Exception ex) { // in case of error
						ex.printStackTrace();
					}
					String content = textArea.getText(); // get the text from textArea
					content = content.replace("remote: Calling...Pick up!", "VoIP call started."); // replace the specific text
					textArea.setText(content); 
				}
				else { // if local starts call
					try {
						String message = ("Calling...Pick up!"); // inform remote local is calling
					    chatUDP.send(message, aesci); // by sending message
					} 
					catch (Exception ex) { // in case of error
						ex.printStackTrace();
					}
					textArea.append("Calling..." + newline); // appear "Calling..." to textArea and change line
				}
				callButton.setText("End Call"); // change button to End Call
				voip.start(); // call method start from VoIP and start VoIP call
				isCallActive = true; // change state when "End Call" is pressed 
			} 
			
			else { // VoIP call not happening
				if (textAreaText.contains("remote: VoIP call ended.")) { // if remote ended call
					String content = textArea.getText(); // get the text from textArea
					content = content.replace("remote: VoIP call ended.", "VoIP call ended."); // replace the specific text
					textArea.setText(content);
				} 
				else { // if local ended call
					try {
						String message = ("VoIP call ended."); // inform remote local has stopped the call
						chatUDP.send(message, aesci); // by sending message
					} 
					catch (Exception ex) { // in case of error
						ex.printStackTrace();
					}
					textArea.append("VoIP call ended."+ newline); // appear "VoIP call ended." to textArea and change line
				}
				callButton.setText("Call"); // change button to Call
				voip.stop(); // call method stop from VoIP and stop VoIP call
				isCallActive = false; // change state when "Call" is pressed
				
				String twoContent = textArea.getText(); // get the text from textArea
				twoContent = twoContent.replace("Calling...", ""); // remove the specific text
				textArea.setText(twoContent);							
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
	public void windowClosing(WindowEvent e) { // close the app
		try {
	        if (chatTCP != null) {
	            chatTCP.closeEverything(); // close streams
	        }
	    }
		catch (Exception ex) { // in case of error
	        ex.printStackTrace();
	    } 
		finally { // always executed
	        dispose();
	        System.exit(0);
	    }
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




