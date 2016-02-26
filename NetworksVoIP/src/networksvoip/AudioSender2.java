package networksvoip;

import CMPC3M06.AudioRecorder;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.sound.sampled.LineUnavailableException;
import static networksvoip.NetworksVoIP.BLOCK_INTERLEAVER_DIM;
import static networksvoip.NetworksVoIP.INTERLEAVING;
import static networksvoip.NetworksVoIP.IP_ADDRESS;
import static networksvoip.NetworksVoIP.MODIFIED;
import uk.ac.uea.cmp.voip.DatagramSocket2;

public class AudioSender2 implements Runnable {

    static DatagramSocket sending_socket;

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        boolean running = true;
        System.out.println("Sending");
        //***************************************************
        //Port to send to
        int PORT = 55555;
        //IP ADDRESS to send to
        InetAddress clientIP = null;
        try {
            clientIP = InetAddress.getByName(IP_ADDRESS);
        } catch (UnknownHostException e) {
            System.out.println("ERROR: TextSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }

        try {
            sending_socket = new DatagramSocket2();
        } catch (SocketException e) {
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }

        AudioRecorder recorder = null;
        try {
            recorder = new AudioRecorder();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(AudioSender2.class.getName()).log(Level.SEVERE, null, ex);
        }

        int counter = 1;
        int blockInterleaverSize = BLOCK_INTERLEAVER_DIM * BLOCK_INTERLEAVER_DIM;
        int blockCounter = 1;
        ArrayList<DatagramPacket> blockInterleaver = new ArrayList<>();
        ArrayList<DatagramPacket> blockInterleaverTemp = new ArrayList<>();
        
        
        
        while (running) {
            try {
                //  4 bytes ordering
                //  8 bytes timestamp

                int headerSize = 8 + 4 + 8;
                int dataSize = 512;
                int blockSize = dataSize + headerSize;
                byte audioData[];
                byte timestamp[];
                byte ordering[];
                byte checksum[];

                if (INTERLEAVING) {
                    while (blockCounter <= blockInterleaverSize) {

                        //  AUDIO DATA
                        audioData = recorder.getBlock();

                        //  HEADER
                        //  timestamp
                        timestamp = Utilities.longToByteArray(System.currentTimeMillis());
                        
                        //  ordering
                        ordering = Utilities.intToByteArray(counter);
                        
                        //  checksum
                        Checksum crcChecksum = new CRC32();
                        crcChecksum.update(audioData, 0, audioData.length);
                        long checksumVal = crcChecksum.getValue();
                        checksum = Utilities.longToByteArray(checksumVal);
                        
                        //  COMPILE PACKET DATA (HEADER + AUDIO)
                        ByteArrayOutputStream compilePacket = new ByteArrayOutputStream();
                        compilePacket.write(ordering);
                        compilePacket.write(timestamp);
                        compilePacket.write(checksum);
                        compilePacket.write(audioData);
                        
                       

                        byte data[] = compilePacket.toByteArray();

                        //Make a DatagramPacket from it, with client address and port number
                        DatagramPacket packet = new DatagramPacket(data, data.length, clientIP, PORT);

                        blockInterleaverTemp.add(packet);

                        counter++;
                        blockCounter++;
                    }
                    blockCounter = 1;

                    blockInterleaver = Utilities.getBlockInterleaver(BLOCK_INTERLEAVER_DIM, blockInterleaverTemp);
                    //Send it
                    for (DatagramPacket int_packet : blockInterleaver) {
                        sending_socket.send(int_packet);
                    }
                    blockInterleaverTemp.clear();
                } else {

                    //  AUDIO DATA
                    audioData = recorder.getBlock();

                        //  HEADER
                    //  timestamp
                    timestamp = Utilities.longToByteArray(System.currentTimeMillis());

                    //  ordering
                    ordering = Utilities.intToByteArray(counter);

                    //  checksum
                    Checksum crcChecksum = new CRC32();
                    crcChecksum.update(audioData, 0, audioData.length);
                    long checksumVal = crcChecksum.getValue();
                    checksum = Utilities.longToByteArray(checksumVal);

                    //  COMPILE PACKET DATA (HEADER + AUDIO)
                    ByteArrayOutputStream compilePacket = new ByteArrayOutputStream();
                    compilePacket.write(ordering);
                    compilePacket.write(timestamp);
                    compilePacket.write(checksum);
                    compilePacket.write(audioData);

                    byte data[] = compilePacket.toByteArray();

                    //Make a DatagramPacket from it, with client address and port number
                    DatagramPacket packet = new DatagramPacket(data, data.length, clientIP, PORT);

                    sending_socket.send(packet);
                    counter++;
                }
            } catch (IOException e) {
                System.out.println("ERROR: TextSender: Some random IO error occured!");
                e.printStackTrace();
            }
        }

        System.out.println("Packets sent:" + counter);
        //Close audio input
        recorder.close();

        //Close the socket
        sending_socket.close();

    }
}
