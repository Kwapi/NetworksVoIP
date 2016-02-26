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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.sound.sampled.LineUnavailableException;
import static networksvoip.NetworksVoIP.ANALYSIS;
import static networksvoip.NetworksVoIP.CONCEALMENT_MODE;
import static networksvoip.NetworksVoIP.GENERAL_PRINTOUTS;
import static networksvoip.NetworksVoIP.REPETITION;
import static networksvoip.NetworksVoIP.SILENCE;
import static networksvoip.Utilities.concealError;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class AudioReceiver4 implements Runnable {

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
            receiving_socket = new DatagramSocket4(PORT);
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
            Logger.getLogger(AudioReceiver4.class.getName()).log(Level.SEVERE, null, ex);
        }

        long timeLastPacketPlayed = System.currentTimeMillis();
        DataPacket previousPacket = new DataPacket();
        while (running) {

            try {
                //Receive a DatagramPacket 
                byte[] buffer = new byte[532];

                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);

                //set timeout length
                receiving_socket.setSoTimeout(4000);

                try {
                    receiving_socket.receive(packet);
                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timed out");
                    running = false;
                } catch (IOException e) {
                    System.out.println("Error in transmission");
                }

                DataPacket currentPacket = new DataPacket(packet.getData());

                if (currentPacket.isCorrupted()) {

                    currentPacket = concealError(previousPacket, currentPacket, CONCEALMENT_MODE);

                } else {
                    if (ANALYSIS) {
                        System.out.println(currentPacket.getId());
                    }
                }
                
                if (GENERAL_PRINTOUTS) {
                    String synthetic = "";
                    if (currentPacket.isSynthetic()) {
                        synthetic = "synthetic";
                    }

                    System.out.printf("\nPacket: \t %d \t Delay: \t%d ms \t %s", currentPacket.getId(), System.currentTimeMillis() - currentPacket.getTimestamp(), synthetic);
                    System.out.println(Arrays.toString(currentPacket.getData()));
                }

                //System.out.println("Playback delay: \t" + (System.currentTimeMillis() - timeLastPacketPlayed));
                player.playBlock(currentPacket.getData());
                //timeLastPacketPlayed = System.currentTimeMillis();
                previousPacket = currentPacket;

            } catch (IOException e) {
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        receiving_socket.close();
        //***************************************************
    }
}
