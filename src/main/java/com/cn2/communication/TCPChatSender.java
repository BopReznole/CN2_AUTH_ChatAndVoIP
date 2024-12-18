package com.cn2.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class TCPChatSender {
	
	private Socket socket; // define socket
	private BufferedReader bufferedReader; // define buffer bufferedReader, contains data sent from remote   
	private BufferedWriter bufferedWriter; // define buffer bufferedWriter, cantains data local will send to remote  
	
	public TCPChatSender(Socket socket) { // conctructor TCPChatSender, initialize Socket
		
		try {
			this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
	}
	
	public void send(String messageToRemote) {
		
		new Thread(() -> { // Thread the send text process
			try {
				bufferedWriter.write(messageToRemote + "\n"); // msgToSend to bufferedWriter 
				bufferedWriter.newLine(); /* scanner.nextLine() leaves line separator \n out of the string 
				so, bufferedWriter.newLine() used to create line separator in buffer */
				bufferedWriter.flush(); // flush the stream when press enter not when buffer is full
				
			}
			catch (IOException e) { // case of error
				e.printStackTrace();
				System.out.println("Error sending message");
				closeEverything(socket, bufferedReader, bufferedWriter);
			}
		}).start(); // start Thread
	}
	
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		
		try {
			   if (socket != null) // checking for null before closing streams to avoid a null pointer exception 
				   socket.close();
			   if (bufferedReader != null)
				   bufferedReader.close();
			   if (bufferedWriter != null)
				   bufferedWriter.close();
		   }
		   catch (IOException e) { // case of error 
				e.printStackTrace();
				}
		}

}
