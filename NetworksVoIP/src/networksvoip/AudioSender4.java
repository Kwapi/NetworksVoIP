package networksvoip;

/*
 * TextSender.java
 *
 * Created on 15 January 2003, 15:29
 */

/**
 *
 * @author  abj
 */
import CMPC3M06.AudioRecorder;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class AudioSender4 implements Runnable{
    
    static DatagramSocket sending_socket;
    
    public void start(){
        Thread thread = new Thread(this);
	thread.start();
    }
    
    public void run (){
     
        System.out.println("Sending");
        //***************************************************
        //Port to send to
        int PORT = 55555;
        //IP ADDRESS to send to
        InetAddress clientIP = null;
	try {
		clientIP = InetAddress.getByName("localhost");
	} catch (UnknownHostException e) {
                System.out.println("ERROR: TextSender: Could not find client IP");
		e.printStackTrace();
                System.exit(0);
	}
        
        try{
		sending_socket = new DatagramSocket4();
	} catch (SocketException e){
                System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
		e.printStackTrace();
                System.exit(0);
	}
       
        Vector<byte[]> voiceVector = new Vector<byte[]>();

        
        AudioRecorder recorder = null;
        try {
            recorder = new AudioRecorder();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(AudioSender4.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        boolean running = true;
        
        int counter = 1;
        while (running){
            try{
                           
                //  4 bytes ordering
                //  8 bytes timestamp
                
                int headerSize = 8 + 4 + 8;
                int dataSize = 512;
                int blockSize = dataSize + headerSize;
                byte audioData[];
                byte timestamp[];
                byte ordering[];
                byte checksum[];
                
                  
                //  AUDIO DATA
                audioData = recorder.getBlock(); 
                
                //  HEADER
                //  timestamp
                timestamp = Utilities.longToByteArray(System.currentTimeMillis());
                
                
                //  ordering
                ordering = Utilities.intToByteArray(counter);
                
                //for testing qos
                voiceVector.add(audioData);
                
                //  checksum
                Checksum crcChecksum = new CRC32();
                crcChecksum.update(audioData,0,audioData.length);
                long checksumVal = crcChecksum.getValue();
                checksum = Utilities.longToByteArray(checksumVal);
                
                
                //  COMPILE PACKET DATA (HEADER + AUDIO)
                ByteArrayOutputStream compilePacket = new ByteArrayOutputStream( );
                compilePacket.write(ordering);
                compilePacket.write(timestamp);
                compilePacket.write(checksum);
                
                compilePacket.write(audioData);
                                
                byte data[] = compilePacket.toByteArray( );
                               
                //Make a DatagramPacket from it, with client address and port number
                DatagramPacket packet = new DatagramPacket(data, data.length, clientIP, PORT);
            
                //Send it
                sending_socket.send(packet);
                                              
                counter++;
            } catch (IOException e){
                System.out.println("ERROR: TextSender: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close audio input
        recorder.close();
        
        //Close the socket
        sending_socket.close();
        
    }
} 
