package modules;

public class DataChunk{

    private long firstByteIndex;
    private int size;
    private byte[] data;

    public DataChunk(long startIndex, int size){
        this.firstByteIndex = startIndex;
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

}
