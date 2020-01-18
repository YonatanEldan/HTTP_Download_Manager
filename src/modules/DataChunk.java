package modules;

import javax.xml.crypto.Data;

public class DataChunk{

        private long firstByteIndex, lastByteIndex;
        byte[] data;
        int size;

        public DataChunk(long startIndex, long endIndexx){
            this.firstByteIndex = firstByteIndex;
            this.lastByteIndex = lastByteIndex;
            this.data = new byte[(int)(lastByteIndex-firstByteIndex)];
            this.size = (int)(lastByteIndex - firstByteIndex);
        }

        public DataChunk(DataChunk dc){
                this(dc.firstByteIndex, dc.lastByteIndex);
                this.data = dc.data;
        }

        public long getFirstByteIndex() {
            return this.firstByteIndex;
        }

        public long getlastByteIndex() { return this.lastByteIndex; }

}
