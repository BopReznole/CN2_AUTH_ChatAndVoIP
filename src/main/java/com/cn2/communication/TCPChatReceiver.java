package com.cn2.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JTextArea;

public class TCPChatReceiver { // class for the user "receiver" that accepts the socket connection request
	
	private ServerSocket serverSocket; // define serverSocket, waits for requests for connection on a port and creates Socket object
	private Socket socket; // define socket
	private BufferedReader bufferedReader; // define buffer bufferedReader, contains data sent from remote   
	private BufferedWriter bufferedWriter; // define buffer bufferedWriter, contains data local will send to remote  
	
	public TCPChatReceiver(ServerSocket serverSocket) { // conctructor TCPChatReceiver, initialize serverSocket and accept socket connection request 
		
		try {
			this.serverSocket = serverSocket;
            this.socket = serverSocket.accept(); // accept serverSocket 
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); /* put InputStreamReader to bufferedReader, 
            InputStreamReader converts the input byte stream coming from socket to character stream */
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); /* put OutputStreamWriter to bufferedWriter, 
            OutputStreamWriter converts the output character stream sent to socket to byte stream */
		}
		catch (IOException e) { // in case of error
            e.printStackTrace();
            System.out.println("Error initializing");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
	}
	
    public void send(String messageToRemote) { // method send, local sends text messageToRemote 
    	
    	try {
			bufferedWriter.write(messageToRemote); // messageToRemote to bufferedWriter 
			bufferedWriter.newLine(); // used to create line separator "\n" in buffer so we know when the messageToRemote is finished 
			bufferedWriter.flush(); // flush the stream when messageToRemote is finished
			
		}
		catch (IOException e) { // in case of error
			e.printStackTrace();
			System.out.println("Error sending message");
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void receive(JTextArea textArea) { // method receive, local receives text messageFromRemote 
		
		new Thread(() -> { // Thread the receive text process 
			while (socket.isConnected()) { // while socket connection is established
				try {
					String messageFromRemote = bufferedReader.readLine(); // messageFromRemote the message remote sends to local 
					textArea.append("remote: " + messageFromRemote + "\n"); /* appear messageFromRemote,string from bufferedReader,
					to textArea and change line */
				}
				catch (IOException e) { // in case of error
					e.printStackTrace();
					System.out.println("Error receiving message");
					closeEverything(socket, bufferedReader, bufferedWriter);
					break; // break from loop 
				}
			}
	    }).start(); // start Thread  	
    }
	
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		
		try {
			   if (socket != null) // checking for null before closing streams to avoid a null pointer exception 
				   socket.close(); // closing streams
			   if (bufferedReader != null)
				   bufferedReader.close();
			   if (bufferedWriter != null)
				   bufferedWriter.close();
		 }
		 catch (IOException e) { // in case of error 
			 e.printStackTrace();
		 }
	}	
	
}
