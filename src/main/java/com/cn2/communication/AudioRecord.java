package com.cn2.communication;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.LineUnavailableException;


public class AudioRecord {
	
	private final TargetDataLine targetLine; /* declare targetLine for capturing audio */
    private final AudioFormat audioFormat; /* declare audio format */
    private final DataLine.Info dataInfo; /* declare info for the audio. */
    private byte[] buffer; /* declare buffer to store stream in */
    
    public AudioRecord() throws LineUnavailableException { /* constructor AudioCapture, initialize variables
    throws LineUnavailableException if audio line is unavailable */

    	this.audioFormat = new AudioFormat(8000, 8, 1, true, false); /* audio format:sampleRate=8000 samples/sec,
	    sampleSize=16 bits,  1 channel, signed (true) PCM, littleEndian (false) */
        this.dataInfo = new DataLine.Info(TargetDataLine.class, audioFormat); /* object dataInfo, contains information
		on what type of audio format targetLine must have */

        if (!AudioSystem.isLineSupported(dataInfo)) { /* audio not supported */
             System.out.println("Not supported");
        }
        
        this.buffer =  new byte[(int) this.audioFormat.getSampleRate()]; /* buffer size according to sampleRate */ 
        this.targetLine = (TargetDataLine) AudioSystem.getLine(dataInfo); /* get targetLine */
    }

    public void open() throws LineUnavailableException { /* method open, 
        throws LineUnavailableException if audioline is unavailable */
    	
        this.targetLine.open(audioFormat); /* open targetLine */
        this.targetLine.start(); /* microphone open, targetLine starts capturing data from microphone */
    }
    
    public byte[] read() {/* method read */
    	
    	targetLine.read(buffer, 0, buffer.length); /* read the captured data into buffer, offset=0 for real time usage */
        return buffer; /* return bytes of data from buffer */ 
	}
	
	public void stop() { /* method stop */
		
		targetLine.stop(); /* stops the targetLine but retains its resources */
		targetLine.close(); /* closes the targetLine and releases resources*/ 
	}
    
}    

    
    

