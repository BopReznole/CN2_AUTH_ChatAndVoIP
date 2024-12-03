package com.cn2.communication; /* file com/cn2/communication */

import com.cn2.communication.ComVoIP; /* import the class from its package-file */
import com.cn2.communication.ComChat; /* import the class from its package-file */


import java.io.*;

import java.net.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
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
	
	private static ComVoIP comVoip; /* declare ComVoIP object for VoIP */
	private static ComChat comChat; /* declare ComChat object for Chat */

	
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
	public static void main(String[] args) throws LineUnavailableException, SocketException {
	
		/*
		 * 1. Create the app's window
		 */
		App app = new App("CN2 - AUTH");  // TODO: You can add the title that will displayed on the Window of the App here																		  
		app.setSize(500,250);				  
		app.setVisible(true);				  

		/*
		 * 2. 
		 */
		
		do { /* local always waiting to receive data, infinite loop */
			try {
				DatagramSocket datagramSocket = new DatagramSocket(); /* define datagramSocket */
				InetAddress remoteAddress = InetAddress.getByName("Localhost"); /* define to inetAddress the IP of remote */
				comVoip = new ComVoIP(datagramSocket, remoteAddress); /* pass datagramSocket, remoteAddress to constructor ComVoIP */
				comVoip.receiveThenSend(); /* call method receiveThenSend from ComVoIP, receive then send audio data */
				comChat = new ComChat(datagramSocket, remoteAddress); /* pass datagramSocket, remoteAddress to constructor ComVoIP */
				comChat.receiveThenSend(textArea); /* call method receiveThenSend from ComChat, receive then send text data */
			}
			catch (IOException e) { /* in case of error */
				e.printStackTrace();
				break; /* break from loop */
			}
		}while(true); 
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
			
			String messageToSend = inputTextField.getText(); /* get string messageToSend from TextField inputTextField */
			if(!messageToSend.isEmpty()) {/* if there is a messageToSend */
				try {
					textArea.append("local: " + messageToSend); /* appear messageToSend to textArea */
					textArea.append("\n"); /* change line */
					inputTextField.setText(""); /* erase messageTosend from inputTextField */
					comChat.send(messageToSend); /* call method send from ComChat, send text data and start communication */
				}
				catch (LineUnavailableException e1) { /* in case of error */
					e1.printStackTrace();
				}
			}
		}
		
		else if (e.getSource() == callButton){ // The "Call" button was clicked 
				
			try {
				comVoip.send(); /* call method send from ComVoIP, send audio data and start audio communication */
				textArea.append("Ongoing call"); /* appear "Ongoing call" to textArea */
				textArea.append("\n"); /* change line */
			}
			catch (LineUnavailableException e1) { /* in case of error */
				e1.printStackTrace();
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
