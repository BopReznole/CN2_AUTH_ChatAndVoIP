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
    private byte[] buffer = new byte[1024]; /* declare buffer to store stream in chunks of 1024 bytes */
    
    public AudioRecord() throws LineUnavailableException { /* constructor AudioCapture, initialize variables
    throws LineUnavailableException if audio line is unavailable */

    	this.audioFormat = new AudioFormat(8000, 8, 1, true, false); /* audio format:sampleRate=8000 samples/sec,
	    sampleSize=8 bits,  1 channel, signed (true) PCM, littleEndian (false) */
        this.dataInfo = new DataLine.Info(TargetDataLine.class, audioFormat); /* object dataInfo, contains information
		on what type of audio format targetLine must have */

        if (!AudioSystem.isLineSupported(dataInfo)) { /* audio not supported */
             System.out.println("Not supported");
        }

        this.targetLine = (TargetDataLine) AudioSystem.getLine(dataInfo); /* get targetLine */
    }

    public byte[] open() throws LineUnavailableException { /* method open, 
        throws LineUnavailableException if audioline is unavailable */

        targetLine.open(audioFormat); /* open targetLine */
        targetLine.start(); /* microphone open, targetLine starts capturing data from microphone */
        targetLine.read(buffer, 0, buffer.length); /* read the captured data into buffer, offset=0 for real time usage */
        return buffer; /* return bytes of data from buffer */ 
    }
    
}    

    
    

