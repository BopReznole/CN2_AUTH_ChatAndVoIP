package com.cn2.communication; 

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.LineUnavailableException;


public class ComVoIP {
	
    private AudioPlayback playback; // declare object for playing sound  
    private AudioRecord record; // declare object for recording sound  
    private InetAddress remoteAddress; // declare IP address remoteAddress, to set it as IP of remote 
    private DatagramSocket datagramSocket; // declare socket datagramSocket 
    private volatile boolean isCalling = false;

    public ComVoIP(DatagramSocket datagramSocket, InetAddress remoteAddress) throws LineUnavailableException {
    // conctructor ComVoIP, initialize datagramSocket, remoteAddress, playback, record  
        
        this.playback = new AudioPlayback();
        this.record = new AudioRecord();
        this.remoteAddress  = remoteAddress;
        this.datagramSocket = datagramSocket;
    }
    
    public void startVoIP() { // method to start VoIP call 
    	
		isCalling = true; // set isCalling to true, VoIP call happening
		try {
			record.open(); /* call method open from AudioRecord, open the targetLine-stream */
			playback.open(); // call method open from AudioPlayback, open the sourceLine-stream */

			// Thread to capture and send audio
			new Thread(() -> {
				try {
					byte[] audioData = new byte[1024]; // audioData, size=1024 bytes, captures audio and returns byte stream
					while (isCalling) {
						audioData = record.read(); // audioData captures audio and returns byte stream 
						DatagramPacket datagramPacket = new DatagramPacket(audioData, audioData.length, remoteAddress, 1243); 
						// get all data from buffer, create a datagramPacket, send to IP remoteAddress and port of remote 
						datagramSocket.send(datagramPacket); // datagramPacket send 
					}
				} 
				catch (Exception e) { // in case of error
					e.printStackTrace();
				}
			}).start(); // start Thread

			// Thread to receive and play audio
			new Thread(() -> {
				try {
					byte[] buffer = new byte[1024]; // buffer, size=1024 bytes, captures audio and returns byte stream 
					while (isCalling) {
						DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length); /* get packet datagramPacket to buffer */
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
		
		isCalling = false; // set isCalling to false, VoIP call not happening
		record.stop();  // call method stop from AudioRecord, close targetLine-stream 
		playback.stop();  // call method stop from AudioPlayback, close sourceLine-stream 
	}
    
}
