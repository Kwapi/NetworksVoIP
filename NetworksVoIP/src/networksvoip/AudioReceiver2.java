package networksvoip;

/*
 * TextReceiver.java
 *
 * Created on 15 January 2003, 15:43
 */
/**
 *
 * @author abj
 */
import CMPC3M06.AudioPlayer;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import static networksvoip.NetworksVoIP.ANALYSIS;
import static networksvoip.NetworksVoIP.BLOCK_INTERLEAVER_DIM;
import static networksvoip.NetworksVoIP.BUFFER_SIZE;
import static networksvoip.NetworksVoIP.CONCEALMENT_MODE;
import static networksvoip.NetworksVoIP.DELAY_ANALYSIS;
import static networksvoip.NetworksVoIP.GENERAL_PRINTOUTS;
import static networksvoip.NetworksVoIP.MODIFIED;
import static networksvoip.NetworksVoIP.REPETITION;
import static networksvoip.NetworksVoIP.SILENCE;
import static networksvoip.Utilities.concealErrorBuffer;
import static networksvoip.Utilities.isError;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class AudioReceiver2 implements Runnable {

    static DatagramSocket receiving_socket;
   
    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {

        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************

        //***************************************************
        //Open a socket to receive from on port PORT
        //DatagramSocket receiving_socket;
        try {
            receiving_socket = new DatagramSocket3(PORT);
        } catch (SocketException e) {
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.
        Vector<byte[]> voiceVector = new Vector<>();

        boolean running = true;
        AudioPlayer player = null;
        int lastPacketReceived = 0;
        try {
            player = new AudioPlayer();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(AudioReceiver3.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ArrayList<Integer> jumbledPackets = new ArrayList<>();
        ArrayList<Integer> lostPackets = new ArrayList<>();
        
        int noPacketsReceived = 0;
        int syntheticCount = 0;
        
        ArrayList<DataPacket> bufferOutput = new ArrayList<>();
        while (running) {

            try {
                //Receive a DatagramPacket 
                byte[] buffer = new byte[524];

                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);

                //set timeout length
                receiving_socket.setSoTimeout(4000);

                try {
                    receiving_socket.receive(packet);
                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timed out");
                    running = false;
                    break;
                } catch (IOException e) {
                    System.out.println("Error in transmission");
                }

                DataPacket currentPacket = new DataPacket(packet.getData());
                
                //System.out.println("RECEIVED \t" + currentPacket.getId());

                if (MODIFIED) {
                    bufferOutput.add(currentPacket);
                    Collections.sort(bufferOutput);

                    while (bufferOutput.size() >= BUFFER_SIZE) {
                        DataPacket current = bufferOutput.get(0);
                        DataPacket next = bufferOutput.get(1);
                        
                        player.playBlock(current.getData());
                        
                        long currentTime = System.currentTimeMillis();
                        if(DELAY_ANALYSIS){
                            System.out.println((currentTime - current.getTimestamp()));
                        }
                        voiceVector.add(current.getData());

                        if (isError(current, next)) {
                            concealErrorBuffer(bufferOutput, CONCEALMENT_MODE);
                        }
                        
                        if(GENERAL_PRINTOUTS){
                            
                            String synthetic = "";
                            if(current.isSynthetic()){
                                synthetic = "synthetic";
                                syntheticCount++;
                            }
                          
                            
                            System.out.println("PLAYING PACKET\t" + current.getId() + "\t" + synthetic);
                        }
                        bufferOutput.remove(0);

                    }
                } else {
                    
                    
                    if(ANALYSIS){
                        System.out.println(currentPacket.getId());
                    }
                                                            
                    player.playBlock(currentPacket.getData());
                    voiceVector.add(currentPacket.getData());
                }

                lastPacketReceived = currentPacket.getId();
                
                noPacketsReceived++;
               
            } catch (IOException e) {
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        receiving_socket.close();
        //***************************************************
        
        System.out.println("Packets received: " + noPacketsReceived);
        System.out.println("Synthetic packets: " + syntheticCount);
        // PLAYBACK
        Iterator<byte[]> voiceItr = voiceVector.iterator();
        while (voiceItr.hasNext()) {
            try {
                player.playBlock(voiceItr.next());
            } catch (IOException ex) {
                Logger.getLogger(AudioReceiver2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //Close audio output
        player.close();

    }
}
