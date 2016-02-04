
package networksvoip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetworksVoIP {
   
    public static int datagramSocketNumber = 1;
    
   public static void main(String[] args) throws IOException {
       BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
       
       AudioReceiver audioReceiver = new AudioReceiver();
       AudioSender audioSender = new AudioSender();
       
       System.out.println("Choose DatagramSocket version: 1,2,3 or 4:");
       
       datagramSocketNumber = Integer.parseInt(in.readLine());
       
       
       audioReceiver.start();
       audioSender.start();
       
       
       
    }
    
}
