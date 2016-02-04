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
import javax.sound.sampled.LineUnavailableException;
import static networksvoip.NetworksVoIP.datagramSocketNumber;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class AudioReceiver implements Runnable {

    static DatagramSocket receiving_socket;

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {

        int PORT = 55555;

        try {
            switch(datagramSocketNumber){
                    case 1: receiving_socket = new DatagramSocket();
                    break;
                    case 2: receiving_socket = new DatagramSocket2();
                        break;
                    case 3: receiving_socket = new DatagramSocket3();
                        break;
                    case 4: receiving_socket = new DatagramSocket4();
                
                }
        } catch (SocketException e) {
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }

        boolean running = true;
        AudioPlayer player = null;
        
        try {
            player = new AudioPlayer();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(AudioReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int lastPacketReceived = 0;
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

                int orderingInt = ConvertUtilities.byteArrayToInt(ordering);
                long timestampLong = ConvertUtilities.byteArrayToLong(timestamp);

                long delay = System.currentTimeMillis() - timestampLong;
                if (orderingInt != lastPacketReceived + 1) {
                    int difference = orderingInt - lastPacketReceived;

                    if (difference < 0) {
                        System.out.print("\n!!!\t ORDERING MISMATCH BY " + difference + "\t!!!");
                    } else {
                        System.out.printf("\n!!!\t %d PACKETS LOST\t\t\t!!!", orderingInt - lastPacketReceived);
                    }

                }

                System.out.printf("\nPacket: \t %d \t Delay: \t%d ms", orderingInt, delay);

                player.playBlock(audio);

                lastPacketReceived = orderingInt;

            } catch (IOException e) {
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        receiving_socket.close();
    }
}
