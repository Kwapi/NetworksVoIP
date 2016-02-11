
package networksvoip;

import java.util.Arrays;

public class NetworksVoIP {
   
   
   public static void main(String[] args) {
       
       int blockInterleaverDim = 2;
       
       AudioReceiver2 audioReceiver = new AudioReceiver2();
       AudioSender2 audioSender = new AudioSender2();
       
       audioReceiver.setBlockInterleaverDimension(blockInterleaverDim);
       audioSender.setBlockInterleaverDimension(blockInterleaverDim);
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
