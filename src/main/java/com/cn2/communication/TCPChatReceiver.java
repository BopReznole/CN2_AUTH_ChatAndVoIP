package com.cn2.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextArea;

public class TCPChatReceiver {
	
	private ServerSocket serverSocket; // define serverSocket, waits for requests for connection on a port and creates Socket object
	private Socket socket; // define socket
	private BufferedReader bufferedReader; // define buffer bufferedReader, contains data sent from remote   
	private BufferedWriter bufferedWriter; // define buffer bufferedWriter, cantains data local will send to remote  
	
	public TCPChatReceiver(ServerSocket serverSocket) { /* conctructor TCPChatReceiver,
		initialize serverSocket and accept socket connection request */
		
		try {
			this.serverSocket = serverSocket;
            this.socket = serverSocket.accept();
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing");
        }
	}
	
	public void receive(JTextArea textArea) {
		
		new Thread(() -> { // Thread the receive text process 
    			try {
					String messageFromRemote = bufferedReader.readLine();
					while ((messageFromRemote = bufferedReader.readLine()) != null) {
		                textArea.append("remote: " + messageFromRemote + "\n");
		            }
				}
				catch (IOException e) { // case of error
					e.printStackTrace();
					System.out.println("Error receiving message");
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
