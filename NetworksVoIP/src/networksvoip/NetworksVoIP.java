
package networksvoip;

import java.util.Arrays;

public class NetworksVoIP {
   
   public static final int REPETITION = 1;
   public static final int SILENCE = 2;
   public static int CONCEALMENT_MODE;
   public static boolean INTERLEAVING;
   static boolean MODIFIED;
   static int BLOCK_INTERLEAVER_DIM;
   static boolean DELAY_ANALYSIS;
   static boolean ANALYSIS;
   static boolean GENERAL_PRINTOUTS;
   static int BUFFER_SIZE;
   public static String IP_ADDRESS;
  
   
   public static void main(String[] args) {
       
       IP_ADDRESS = "CMPLEWIN-03";
       
       MODIFIED = true;
       BUFFER_SIZE = 9;
       CONCEALMENT_MODE = REPETITION;
       
       INTERLEAVING = true;
       BLOCK_INTERLEAVER_DIM = 3;
       
       DELAY_ANALYSIS = false;
       ANALYSIS = false;
       GENERAL_PRINTOUTS = false;
       
       
     
       AudioReceiver2 audioReceiver = new AudioReceiver2();
       AudioSender2 audioSender = new AudioSender2();
       
       audioReceiver.start();
       audioSender.start();
              
       
                
             
    }
    
}
