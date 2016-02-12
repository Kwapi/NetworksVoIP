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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import static networksvoip.NetworksVoIP.BLOCK_INTERLEAVER_DIM;
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

        int PORT = 55555;
        try {
            receiving_socket = new DatagramSocket2(PORT);
        } catch (SocketException e) {
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.
        boolean running = true;
        AudioPlayer player = null;

        try {
            player = new AudioPlayer();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(AudioReceiver2.class.getName()).log(Level.SEVERE, null, ex);
        }

        Vector<byte[]> voiceVector = new Vector<>();
        int lastPacketReceived = 0;
        int noPacketsSinceError = 0;
        ArrayList<Integer> packetsLostArr = new ArrayList<>();
        ArrayList<Integer> packetsReceivedArr = new ArrayList<>();

        int lastPlayed = 0;
        
        final int BUFFER_SIZE = BLOCK_INTERLEAVER_DIM*BLOCK_INTERLEAVER_DIM;
        
        TreeMap<Integer, byte[]> audioBuffMap = new TreeMap<Integer, byte[]>();

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
                } catch (IOException e) {
                    System.out.println("Error in transmission");
                }

                DataPacket currentPacket = new DataPacket(packet.getData());

                long delay = System.currentTimeMillis() - currentPacket.getTimestamp();

                // PACKETS LOST OR WRONG ORDER
                /*
                 if(orderingInt != lastPacketReceived + 1){
                    
                 //System.out.print("\nRECEIVED: " + noPacketsSinceError);
                 packetsReceivedArr.add((Integer)noPacketsSinceError);
                 noPacketsSinceError = 0;
                    
                 int packetsLost = orderingInt - lastPacketReceived - 1;
                 //System.out.printf("\nLOST:%d",packetsLost);
                    
                 // testing
                 packetsLostArr.add((Integer)packetsLost);
                 }
                 */
                //System.out.printf("\nPacket: \t %d \t Delay: \t%d ms",orderingInt,delay);
               
                
                bufferOutput.add(currentPacket);
                Collections.sort(bufferOutput);
                
                while(bufferOutput.size() >= BUFFER_SIZE){
                    DataPacket dataPacket = bufferOutput.get(0);
                    
                    player.playBlock(dataPacket.getData());
                    
                    
                    System.out.println("PLAYING PACKET\t" + dataPacket.getId());
                    bufferOutput.remove(0);
                    
                    
                }
                
                
                lastPacketReceived = currentPacket.getId();
                noPacketsSinceError++;

                
                
                
                
                
                
                if (lastPacketReceived == 1000) {
                    break;
                }
            } catch (IOException e) {
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        
        
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

        //Close the socket
        receiving_socket.close();
        
    }
}

/*
// RECEIVING BLOCK INTERLEAVER
                // SORTING AND DECODING
                if (audioBuffMap.size() != BLOCK_INTERLEAVER_DIM*BLOCK_INTERLEAVER_DIM) {
                    audioBuffMap.put(orderingInt, audio);
                    voiceVector.add(audio);
                } else {

                    int currentIndex = 0;
                    for (Map.Entry<Integer, byte[]> entry : audioBuffMap.entrySet()) {
                        player.playBlock(entry.getValue());
                        currentIndex = entry.getKey();
                        
                        System.out.println("Current Packet:\t" + currentIndex + "\tPackets lost:   " + (currentIndex - lastPlayed - 1));
                        
                        lastPlayed = currentIndex;
                    }

                    audioBuffMap.clear();
                }

*/