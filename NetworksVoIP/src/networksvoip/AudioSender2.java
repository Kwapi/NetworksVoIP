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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import static networksvoip.Utilities.flatten;

import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class AudioSender2 implements Runnable{
    
    static DatagramSocket sending_socket;
    static int BLOCK_INTERLEAVER_DIM = 0;
    
    public void setBlockInterleaverDimension(int dim){
        BLOCK_INTERLEAVER_DIM = dim;
    }
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
		sending_socket = new DatagramSocket2();
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
            Logger.getLogger(AudioSender2.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        boolean running = true;
        
        
        ByteBuffer byteBufferLong = ByteBuffer.allocate(Long.BYTES);
        ByteBuffer byteBufferInt = ByteBuffer.allocate(4);
        int counter = 1;
        int blockInterleaverSize = BLOCK_INTERLEAVER_DIM * BLOCK_INTERLEAVER_DIM;
        int blockCounter = 1;
        ArrayList<DatagramPacket> blockInterleaver = new ArrayList<>();
        ArrayList<DatagramPacket> blockInterleaverTemp = new ArrayList<>();
        
        while (counter<=1000){
            try{
                
                while(blockCounter<=blockInterleaverSize){
                    //  4 bytes ordering
                    //  8 bytes timestamp

                    int headerSize = 8 + 4;
                    int dataSize = 512;
                    int blockSize = dataSize + headerSize;
                    byte audioData[];
                    byte timestamp[];
                    byte ordering[];


                    //  AUDIO DATA
                    audioData = recorder.getBlock(); 

                    //  HEADER
                    //  timestamp
                    timestamp = Utilities.longToByteArray(System.currentTimeMillis());


                    //  ordering
                    ordering = Utilities.intToByteArray(counter);




                    //  COMPILE PACKET DATA (HEADER + AUDIO)
                    ByteArrayOutputStream compilePacket = new ByteArrayOutputStream( );
                    compilePacket.write(ordering);
                    compilePacket.write(timestamp);
                    compilePacket.write(audioData);

                    byte data[] = compilePacket.toByteArray( );

                    //Make a DatagramPacket from it, with client address and port number
                    DatagramPacket packet = new DatagramPacket(data, data.length, clientIP, PORT);
                    
                    blockInterleaverTemp.add(packet);
                    
                    counter++;
                    blockCounter++;
                }
                blockCounter = 1;
                
                
                
                blockInterleaver = Utilities.getBlockInterleaver(BLOCK_INTERLEAVER_DIM, blockInterleaverTemp);
                //Send it
                for (DatagramPacket packet : blockInterleaver){
                    sending_socket.send(packet);
                }
                blockInterleaverTemp.clear();
                
                
                                              
                
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
