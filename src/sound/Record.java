/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sound;

/**
 *
 * @author Shaplygin
 */

import java.io.File;  
import java.io.IOException;  

import javax.sound.sampled.AudioFileFormat;  
import javax.sound.sampled.AudioFileFormat.Type;  
import javax.sound.sampled.AudioFormat;  
import javax.sound.sampled.AudioInputStream;  
import javax.sound.sampled.AudioSystem;  
import javax.sound.sampled.DataLine; 
import javax.sound.sampled.TargetDataLine;  

public class Record extends Thread    {  
  private TargetDataLine        m_line;  
  private AudioFileFormat.Type    m_targetType;  
  private AudioInputStream    m_audioInputStream;  
  private File            m_outputFile;  
  private String          m_path;

  public Record(TargetDataLine m_line, Type m_targetType,  File m_outputFile, String m_path) {  
      this.m_line = m_line;  
      this.m_targetType = m_targetType;  
      this.m_audioInputStream = new AudioInputStream(m_line);  
      this.m_outputFile = m_outputFile;  
      this.m_path = m_path;
  }  

  public Record() throws Exception{
    String myJarPath = Record.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    String dirPath = new File(myJarPath).getParent();

    AudioFormat    audioFormat = new AudioFormat(  
            AudioFormat.Encoding.PCM_SIGNED,  
            22050.0F, 16, 1, 2, 22050.0F, false);  

     
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);  
      TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info); 
      targetDataLine.open(audioFormat);
      
      this.m_line = targetDataLine;
      this.m_audioInputStream = new AudioInputStream(m_line); 
      this.m_targetType = AudioFileFormat.Type.WAVE; 
      dirPath = dirPath+"\\audiorec.wav";
      this.m_path = dirPath;
      File outputFile = new File(dirPath);
      this.m_outputFile = outputFile;  
  }
  
  public void startRec() {  
      m_line.start();  
      super.start();  
  }  

  public void stopRec() {  
      m_line.stop();  
      m_line.close();
  }
  public String getPath(){
      return this.m_path;
  }
  
  @Override
  public void run() {  
      try {  
      AudioSystem.write(  
      m_audioInputStream,  
      m_targetType,  
      m_outputFile);  
      } catch (IOException e) {  
          e.printStackTrace();  
      }  
  } 
}  
