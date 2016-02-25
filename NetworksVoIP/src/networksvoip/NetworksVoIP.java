
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
  
   
   public static void main(String[] args) {
       
       MODIFIED = false;
       BUFFER_SIZE = 9;
       CONCEALMENT_MODE = REPETITION;
       
       INTERLEAVING = false;
       BLOCK_INTERLEAVER_DIM = 4;
       
       DELAY_ANALYSIS = false;
       ANALYSIS = false;
       GENERAL_PRINTOUTS = false;
       
       
     
       AudioReceiver3 audioReceiver = new AudioReceiver3();
       AudioSender3 audioSender = new AudioSender3();
       
       audioReceiver.start();
       audioSender.start();
              
       
                
             
    }
    
}
