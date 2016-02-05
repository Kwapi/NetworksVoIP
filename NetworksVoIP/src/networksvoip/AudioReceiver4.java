package networksvoip;

/*
 * TextReceiver.java
 *
 * Created on 15 January 2003, 15:43
 */

/**
 *
 * @author  abj
 */
import CMPC3M06.AudioPlayer;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class AudioReceiver4 implements Runnable{
    
    static DatagramSocket receiving_socket;
    
 public void start(){
        Thread thread = new Thread(this);
	thread.start();
    }
    
    public void run (){
     
        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************
        
        //***************************************************
        //Open a socket to receive from on port PORT
        
        //DatagramSocket receiving_socket;
        try{
		receiving_socket = new DatagramSocket4(PORT);
	} catch (SocketException e){
                System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
		e.printStackTrace();
                System.exit(0);
	}
        //***************************************************
        
        //***************************************************
        //Main loop.
        
        boolean running = true;
        AudioPlayer player = null;
        int lastPacketReceived = 0;
        try {
            player = new AudioPlayer();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(AudioReceiver4.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ByteBuffer byteBufferInt = ByteBuffer.allocate(4);
        ByteBuffer byteBufferLong = ByteBuffer.allocate(Long.BYTES);
        while (running){
         
            try{
                //Receive a DatagramPacket 
                byte[] buffer = new byte[524];
                
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);

                //set timeout length
                receiving_socket.setSoTimeout(4000);
                
                try{
                    receiving_socket.receive(packet);
                }catch (SocketTimeoutException e){
                    System.out.println("Socket timed out");
                } catch (IOException e){
                      System.out.println("Error in transmission");
                }
                
                
                byte[] ordering = Arrays.copyOfRange(buffer, 0, 4);
                byte[] timestamp = Arrays.copyOfRange(buffer,4,12);
                byte[] audio = Arrays.copyOfRange(buffer, 12, 524);
                
                
                int orderingInt = ConvertUtilities.byteArrayToInt(ordering);
                long timestampLong = ConvertUtilities.byteArrayToLong(timestamp);
                
                long delay = System.currentTimeMillis() - timestampLong;
                if(orderingInt != lastPacketReceived + 1){
                    int difference = orderingInt - lastPacketReceived;
                    
                    if(difference <0){
                        System.out.print("\n!!!\t ORDERING MISMATCH BY " + difference +"\t!!!");
                        System.out.println(Arrays.toString(buffer));
                    }else{
                        System.out.printf("\n!!!\t %d PACKETS LOST\t\t\t!!!",orderingInt - lastPacketReceived - 1);
                    }
                    
                }
                
                System.out.printf("\nPacket: \t %d \t Delay: \t%d ms",orderingInt,delay);
                
                player.playBlock(audio);
                
                lastPacketReceived = orderingInt;
                
            } catch (IOException e){
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        receiving_socket.close();
        //***************************************************
    }
}
