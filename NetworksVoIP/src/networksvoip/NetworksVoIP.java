
package networksvoip;

import java.util.Arrays;

public class NetworksVoIP {
   
   public static final int REPETITION = 1;
   public static final int SILENCE = 2;
   static boolean MODIFIED;
   static int BLOCK_INTERLEAVER_DIM;
   
   public static void main(String[] args) {
       
       MODIFIED = false;
       BLOCK_INTERLEAVER_DIM = 4;
       
       AudioReceiver3 audioReceiver = new AudioReceiver3();
       AudioSender3 audioSender = new AudioSender3();
       
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
