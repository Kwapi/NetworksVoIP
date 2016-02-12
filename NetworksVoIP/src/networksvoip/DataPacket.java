
package networksvoip;

import java.util.Arrays;

public class DataPacket implements Comparable{
    private byte [] packet;
    private byte [] data;
    private int id;
    private long timestamp;
    private boolean synthetic;
    
    public DataPacket(byte[] packet){
        this.packet = packet;
        initialise();
       
    }

    public void setPacket(byte[] packet) {
        this.packet = packet;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * COPY CONSTRUCTOR
     * @param other 
     */
    public DataPacket(DataPacket other){
       this.packet = other.getPacket();
       
       this.data = other.getData();
       this.id = other.getId();
       this.synthetic = other.isSynthetic();
       this.timestamp = other.getTimestamp();
    }
    
    public final void initialise(){
        byte[] orderingByte = Arrays.copyOfRange(packet, 0, 4);
        byte[] timestampByte = Arrays.copyOfRange(packet, 4, 12);
        
        
        data = Arrays.copyOfRange(packet, 12, 524);
        id = Utilities.byteArrayToInt(orderingByte);
        timestamp = Utilities.byteArrayToLong(timestampByte);
        synthetic = false;
    }

    public byte[] getData() {
        return data;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(Object t) {
        
        return Integer.compare(this.id, ((DataPacket)t).getId());
       
    }
    
    public byte[] getPacket(){
        return packet;
    }

    public boolean isSynthetic() {
        return synthetic;
    }

    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }
    
    
    
    
    
    
    
    
    
}
