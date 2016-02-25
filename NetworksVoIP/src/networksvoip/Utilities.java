/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package networksvoip;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import static networksvoip.NetworksVoIP.REPETITION;
import static networksvoip.NetworksVoIP.SILENCE;

/**
 *
 * @author vtv13qau
 */
public class Utilities {
    
    
    
    
    public static boolean isError(DataPacket current, DataPacket next){
     
        return next.getId() != current.getId() + 1;
    }
    
    public static void concealErrorBuffer(ArrayList<DataPacket> buffer, int type){
        DataPacket fillerPacket = null;
        switch (type){
            case REPETITION:
                fillerPacket= new DataPacket(buffer.get(0));
                fillerPacket.setSynthetic(true);
                fillerPacket.setId(fillerPacket.getId() + 1);
                buffer.add(1, fillerPacket);
                
                break;
            case SILENCE:
                fillerPacket = new DataPacket(buffer.get(0));
                fillerPacket.setSynthetic(true);
                fillerPacket.setId(fillerPacket.getId() + 1);
                byte[] silence = new byte[fillerPacket.getData().length];
                
                fillerPacket.setData(silence);
                buffer.add(1, fillerPacket);
                break;
        }
        
    }
    
    public static DataPacket concealError(DataPacket previous, DataPacket current, int type){
        
        if(current.getId() <= 1){
            type = SILENCE;
        }
        
        DataPacket fillerPacket = null;
        switch (type){
            case REPETITION:
                fillerPacket= new DataPacket(previous);
                fillerPacket.setSynthetic(true);
                fillerPacket.setId(fillerPacket.getId() + 1);
                break;
            case SILENCE:
                fillerPacket= new DataPacket(previous);
                fillerPacket.setSynthetic(true);
                fillerPacket.setId(fillerPacket.getId() + 1);
                byte[] silence = new byte[current.getData().length];
                
                fillerPacket.setData(silence);
               break;
        }
        
        return fillerPacket;
    }
    
    
    public static ArrayList<DatagramPacket> getBlockInterleaver (int blockInterleaverSize, ArrayList<DatagramPacket> original){
        
        ArrayList<DatagramPacket> mumbledArr = new ArrayList<>();
        for (int i = 0; i < blockInterleaverSize; i++){
                        for (int j = 0; j < blockInterleaverSize; j++){
                            int newIndex = i*blockInterleaverSize + j;
                            int interleavedIndex = getInterleavedIndex(i,j,blockInterleaverSize);
                            
                            
                            mumbledArr.add(newIndex,original.get(interleavedIndex));                                 
                                   
                        }
                    }
        
        return mumbledArr;
    }
    public static int getInterleavedIndex(int i,int j, int blockInterleaverSize){
        int newIndex =0;
            newIndex = j*blockInterleaverSize + (blockInterleaverSize-1-i);
        
        return newIndex;
        
    }
    
    public static byte[] flatten(byte[]... arrs) {
        int L = 0;
        for (byte[] arr : arrs) {
            L += arr.length;
        }
        byte[] ret = new byte[L];
        int start = 0;
        for (byte[] arr : arrs) {
            System.arraycopy(arr, 0, ret, start, arr.length);
            start += arr.length;
        }
        return ret;
    }
    
   public static int byteArrayToInt(byte[] b) {
    final ByteBuffer bb = ByteBuffer.wrap(b);
    return bb.getInt();
}

public static byte[] intToByteArray(int i) {
    final ByteBuffer bb = ByteBuffer.allocate(4);
    bb.putInt(i);
    return bb.array();
}

public static byte[] longToByteArray(long i){
    final ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
    bb.putLong(i);
    return bb.array();
}

public static long byteArrayToLong(byte[] b){
    final ByteBuffer bb = ByteBuffer.wrap(b);
    return bb.getLong();
}
}
