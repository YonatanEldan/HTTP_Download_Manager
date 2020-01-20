package modules;

public class DataChunk{

    private long firstByteIndex;
    private int size;
    private byte[] data;

    public DataChunk(long firstByteIndex, int size){
        this.firstByteIndex = firstByteIndex;
        this.size = size;
        this.data = new byte[size];

    }

    public long getFirstByteIndex() {
        return firstByteIndex;
    }

    public int getSize() {
        return size;
    }

    public byte[] getData() {
        return data;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
