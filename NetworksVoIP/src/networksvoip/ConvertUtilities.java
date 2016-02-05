/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package networksvoip;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author vtv13qau
 */
public class ConvertUtilities {
    
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
