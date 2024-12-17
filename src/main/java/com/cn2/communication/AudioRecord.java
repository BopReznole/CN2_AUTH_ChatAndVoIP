package com.cn2.communication;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.LineUnavailableException;
import java.io.ByteArrayOutputStream;


public class AudioRecord { // class for recording sound
	
	private final TargetDataLine targetLine; // define targetLine for capturing audio 
    private final AudioFormat audioFormat; // define audio format 
    private final DataLine.Info dataInfo; // define info for the audio. 
    private byte[] buffer = new byte[1024]; // define buffer to store stream in 
    
    public AudioRecord() throws LineUnavailableException { /* constructor AudioCapture, initialize variables
    throws LineUnavailableException if audio line is unavailable */

    	this.audioFormat = new AudioFormat(44100, 16, 1, true, false); /* audio format:sampleRate=44100 samples/sec,
	    sampleSize=32 bits,  1 channel, signed (true) PCM, littleEndian (false) */
        this.dataInfo = new DataLine.Info(TargetDataLine.class, audioFormat); /* object dataInfo, contains information
		on what type of audio format targetLine must have */

        if (!AudioSystem.isLineSupported(dataInfo)) { // check if audio is supported 
             System.out.println("Not supported");
        }
         
        this.targetLine = (TargetDataLine) AudioSystem.getLine(dataInfo); // get targetLine 
    }

    public void open() throws LineUnavailableException { /* method open, 
        throws LineUnavailableException if audioline is unavailable */
    	
        this.targetLine.open(audioFormat); // open targetLine 
        this.targetLine.start(); // microphone open, targetLine starts capturing data from microphone 
    }
    
    public byte[] read() { // method read 
    	
    	targetLine.read(buffer, 0, buffer.length); /* read the captured data into buffer, offset=0 for real time usage */
        return buffer; /* return bytes of data from buffer */ 
	 }
	
	public void stop() { // method stop 
		
		targetLine.stop(); // stops the targetLine but retains its resources 
		targetLine.close(); // closes the targetLine and releases resources 
	}
    
}    

    
    

