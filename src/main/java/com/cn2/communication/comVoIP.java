package com.cn2.communication;

import com.cn2.communication.AudioPlayback; /* import the class from its package-file */
import com.cn2.communication.AudioRecord; /* import the class from its package-file */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.LineUnavailableException;


public class comVoIP {
	
    private AudioPlayback playback; /* declare object for playing sound  */
    private AudioRecord record; /* declare object for recording sound  */
    private InetAddress remoteAddress; /* declare IP address remoteAddress, to set it as IP of remote */
    private DatagramPacket datagramPacket; /* declare datagramPacket the datagramPacket sent or recieved */
    private DatagramSocket datagramSocket; /* declare socket datagramSocket */
    private byte[] buffer = new byte[1024]; /* declare buffer to store messages, size = 1024 bytes */

    public comVoIP(DatagramSocket datagramSocket, InetAddress remoteAddress) throws LineUnavailableException {
    	/* conctructor comVoIP, initialize datagramSocket, localAddress, remoteAddress,  */
        
        this.playback = new AudioPlayback();
        this.record = new AudioRecord();
        this.remoteAddress  = remoteAddress;
        this.datagramSocket = datagramSocket;
    }
    
    public void sendAudio() throws LineUnavailableException {
    	Thread sendAudioThread = new Thread() { /* thread the sending audio process  */
    		@Override public void run() {
    			try {
    				record.open(); /* call method open from AudioRecord, open the targetLine-stream */
    				buffer = record.read(); /* buffer captures audio and returns byte stream */
    				datagramPacket = new DatagramPacket(buffer, buffer.length, remoteAddress, 1234); /* get all data from buffer,
    				create a datagramPacket, send to IP inetAddress and port of remote */
    				datagramSocket.send(datagramPacket); /* datagramPacket send */
    			}
    			catch (IOException | LineUnavailableException e) { /* in case of error */
    				e.printStackTrace();
    			}
    				
    			record.stop(); /* call method stop from AudioRecord, close targetLine-stream */
    		}
        };
        sendAudioThread.start(); /* start thread */
    }
    
    public void receiveAudio() throws LineUnavailableException {
    	Thread receiveAudioThread = new Thread() { /* thread the receiving audio process  */
    		@Override public void run() {
    			try {
    				playback.open(); /* call method open from AudioPlayback, open the sourceLine-stream */
    				datagramPacket = new DatagramPacket(buffer, buffer.length); /* get packet datagramPacket to buffer */
    				datagramSocket.receive(datagramPacket); /* datagramPacket received from datagramSocket, blocking method */
    				InetAddress remoteAddress = datagramPacket.getAddress(); /* get IP address remoteAddress */
    				int port = datagramPacket.getPort(); /* get port */
    		        playback.write(buffer); /* call method write from AudioPlayback, play the audio */
    			}
    			catch (IOException | LineUnavailableException e) { /* in case of error */
    				e.printStackTrace();
    			}
    	    	
    	    	playback.stop();
    		}
    	};
    	receiveAudioThread.start(); /* start thread */
    	
    }
    
}
