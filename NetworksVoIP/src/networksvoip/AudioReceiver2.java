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
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class AudioReceiver2 implements Runnable {

    static DatagramSocket receiving_socket;
    static int BLOCK_INTERLEAVER_DIM = 0;
    
    public void setBlockInterleaverDimension(int dim){
        BLOCK_INTERLEAVER_DIM = dim;
    }
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

        TreeMap<Integer, byte[]> audioBuffMap = new TreeMap<Integer, byte[]>();
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

                byte[] ordering = Arrays.copyOfRange(buffer, 0, 4);
                byte[] timestamp = Arrays.copyOfRange(buffer, 4, 12);
                byte[] audio = Arrays.copyOfRange(buffer, 12, 524);

                int orderingInt = Utilities.byteArrayToInt(ordering);
                long timestampLong = Utilities.byteArrayToLong(timestamp);

                long delay = System.currentTimeMillis() - timestampLong;

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
                
                lastPacketReceived = orderingInt;
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
