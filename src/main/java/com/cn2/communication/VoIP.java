package com.cn2.communication; 

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.sound.sampled.LineUnavailableException;


public class VoIP {
	
    private AudioPlayback playback; // define object for playing sound  
    private AudioRecord record; // define object for recording sound  
    private InetAddress remoteAddress; // define IP address remoteAddress, to set it as IP of remote 
    private DatagramSocket datagramSocket; // define DatagramSocket datagramSocket 
    private volatile boolean isCallActive = false; // VoIP call not happening

    public VoIP(DatagramSocket datagramSocket, InetAddress remoteAddress) throws LineUnavailableException {
    // conctructor VoIP, initialize datagramSocket, remoteAddress, playback, record  
        
        this.playback = new AudioPlayback();
        this.record = new AudioRecord();
        this.remoteAddress  = remoteAddress;
        this.datagramSocket = datagramSocket;
    }
    
    public void startVoIP() { // method to start VoIP call 
    	
    	isCallActive = true; // set isCallActive to true, VoIP call happening
		try {
			record.open(); /* call method open from AudioRecord, open the targetLine-stream */
			playback.open(); // call method open from AudioPlayback, open the sourceLine-stream */

			new Thread(() -> { // Thread the capture and send audio process
				try {
					byte[] sendAudioBuffer = new byte[1024]; // sendAudioBuffer, size=1024 bytes, captures audio and returns byte stream
					while (isCallActive) { // while VoIP call is happening
						sendAudioBuffer = record.read(); // sendAudioBuffer captures audio and returns byte stream 
						DatagramPacket datagramPacket = new DatagramPacket(sendAudioBuffer, sendAudioBuffer.length, remoteAddress, 1243); 
						// get all data from buffer, create a datagramPacket, send to IP remoteAddress and port of remote 
						datagramSocket.send(datagramPacket); // datagramPacket send 
					}
				} 
				catch (Exception e) { // in case of error
					e.printStackTrace();
				}
			}).start(); // start Thread

			
			new Thread(() -> { // Thread the receive and play audio process
				try {
					byte[] receiveAudioBuffer = new byte[1024]; // receiveAudioBuffer, size=1024 bytes, captures byte stream and returns audio 
					while (isCallActive) { // while VoIP call is happening
						DatagramPacket datagramPacket = new DatagramPacket(receiveAudioBuffer, receiveAudioBuffer.length);
						// get packet datagramPacket to receiveAudioBuffer 
						datagramSocket.receive(datagramPacket); /* datagramPacket received from datagramSocket, blocking method */
						playback.write(datagramPacket.getData()); /* call method write from AudioPlayback, play the audio */
					}
				} 
				catch (Exception e) { // in case of error
					e.printStackTrace();
				}
			}).start(); // start Thread

		} 
		catch (Exception e) { // in case of error
			e.printStackTrace();
		}
	}

	public void stopVoIP() { // method to stop VoIP call 
		
		isCallActive = false; // set isCallActive to false, VoIP call not happening
		record.stop();  // call method stop from AudioRecord, close targetLine-stream 
		playback.stop();  // call method stop from AudioPlayback, close sourceLine-stream 
	}
    
}
