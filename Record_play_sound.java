import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Record_play_sound {

	public static void main(String[] args) {
		
		try                
		{
		   AudioFormat audioFormat = new AudioFormat(16000, 8, 1, true, true); /* sampleRate=8000 samples/sec,
		    sampleSize=8 bits,  1 channel, signed (true) PCM, bigEndian (true)*/
		   
		   DataLine.Info dataInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
		   if (!AudioSystem.isLineSupported(dataInfo)) /* audio supported  */ 
		   {   
			   System.out.println("Not supported");
		   }
		   
		   TargetDataLine targetLine = (TargetDataLine)AudioSystem.getLine(dataInfo);
		   targetLine.open(); /* microphone ready */
		   
		   JOptionPane.showMessageDialog(null, "Hit ok to start recording");
		   targetLine.start(); /* microphone open, targetLine reading data form microphone */
		   
		   Thread audioRecorderThread = new Thread()
			{
			   @Override public void run()
			   {
				   AudioInputStream  recordingStream = new AudioInputStream(targetLine); /* targetLine stream input
				   to recordingStream */
				   File outputFile = new  File("record.wav"); /* create outputFile file */
				   
				  try 
				  {
					  AudioSystem.write(recordingStream, AudioFileFormat.Type.WAVE, outputFile); /* recordingStream
					   to file */
				  }
				  catch (IOException ex) /* in case of error */
				  {
					  System.out.println(ex);
				  }
				  
				  System.out.println("Stopped recording");
			   }
			};
			
			audioRecorderThread.start(); /* start thread */
			JOptionPane.showMessageDialog(null, "Hit ok to stop recording");
			targetLine.stop();
			targetLine.close();
		   
		}   
		catch (Exception e)
		{
			System.out.println(e);
		}

	}

}
