package com.cn2.communication;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.LineUnavailableException;


public class AudioPlayback {
	
    private final SourceDataLine sourceLine; /* declare sourceLine for playing audio */
    private final AudioFormat audioFormat; /* declare audio format */
    private final DataLine.Info dataInfo; /* declare info for the audio. */
    private byte[] buffer = new byte[1024]; /* declare buffer to store stream in chunks of 1024 bytes */
    
	public AudioPlayback() throws LineUnavailableException { /* constructor AudioPlay, initialize variables
	    throws LineUnavailableException if audio line is unavailable */
		
		this.audioFormat = new AudioFormat(8000, 8, 1, true, false); /* audio format:sampleRate=8000 samples/sec,
	    sampleSize=8 bits,  1 channel, signed (true) PCM, littleEndian (false) */
		this.dataInfo = new DataLine.Info(SourceDataLine.class, audioFormat); /* object dataInfo, contains information
		on what type of audio format sourceLine must have */
		this.sourceLine = (SourceDataLine) AudioSystem.getLine(dataInfo); /* get sourceLine */
		
	}
	
	public void open() throws LineUnavailableException { /* method open, 
        throws LineUnavailableException if audioline is unavailable */

        sourceLine.open(audioFormat); /* open sourceLine */
        sourceLine.start(); /* speaker open, sourceLine starts playing audio from speaker */
        sourceLine.write(buffer, 0, buffer.length); /* write the producing data into buffer, offset=0 for real time usage */
    }
	
}