
package networksvoip;

import java.util.Arrays;

public class NetworksVoIP {
   
   public static final int REPETITION = 1;
   public static final int SILENCE = 2;
   public static boolean INTERLEAVING;
   static boolean MODIFIED;
   static int BLOCK_INTERLEAVER_DIM;
   static boolean DELAY_ANALYSIS;
   static boolean ANALYSIS;
   static boolean GENERAL_PRINTOUTS;
   static int BUFFER_SIZE;
  
   
   public static void main(String[] args) {
       
       MODIFIED = false;
       
        BUFFER_SIZE = 16;
       INTERLEAVING = false;
        BLOCK_INTERLEAVER_DIM = 4;
       
       DELAY_ANALYSIS = false;
       ANALYSIS = true;
       GENERAL_PRINTOUTS = false;
       
       
     
       AudioReceiver2 audioReceiver = new AudioReceiver2();
       AudioSender2 audioSender = new AudioSender2();
       
       audioReceiver.start();
       audioSender.start();
              
       
       /*
       int[] original = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
       int[] modified = new int[16];
       int d = 4;
       
       for (int i=0; i<d;i++){
           for (int j=0; j<d;j++){
               int newIndex = Utilities.getInterleavedIndex(i, j, d);
               modified[i*d + j] = original[newIndex];
           }
       }
       
       
       System.out.println(Arrays.toString(original));
       System.out.println(Arrays.toString(modified));
               
               */
    }
    
}
