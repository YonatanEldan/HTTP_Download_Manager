package modules;

public class DataChunk{

        long firstByteIndex, lastByteIndex;
        Byte[] data;

        public DataChunk(long startIndex, long endIndexx){
            this.firstByteIndex = firstByteIndex;
            this.lastByteIndex = lastByteIndex;
            this.data = new Byte[(int)(lastByteIndex-firstByteIndex)];
        }
}
