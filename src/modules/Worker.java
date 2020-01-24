package modules;

import Constants.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;

public class Worker implements Runnable {
    private String url;
    private long firstByteIndex, curByteIndex,lastByteIndex;
    private int sizeOfChunk;
    private ArrayBlockingQueue<DataChunk> queue;

    private InputStream inputStream;

    public Worker(long firstByteIndex, long lastByteIndex, String url, int sizeOfChunk, ArrayBlockingQueue<DataChunk> queue){
        this.firstByteIndex = firstByteIndex;
        this.curByteIndex = firstByteIndex;
        this.lastByteIndex = lastByteIndex;
        this.url = url;
        this.sizeOfChunk = sizeOfChunk;
        this.queue = queue;
    }

    @Override
    public void run() {

        //connect to server
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Range", "bytes=" + this.firstByteIndex + "-" + this.lastByteIndex);

            this.inputStream = connection.getInputStream();

        } catch(IOException e){
            System.err.println(RuntimeMessages.SERVER_CONNECTION_FAILED);
        }

        // read chunks and write to queue
        try {
            DataChunk currDataChunk = new DataChunk(this.curByteIndex, this.sizeOfChunk);
            int bytesRead = this.inputStream.read(currDataChunk.getData());
            while (bytesRead != -1) {
                currDataChunk.setSize(bytesRead);
                writeToQueue(currDataChunk);

                this.curByteIndex += bytesRead;
                currDataChunk = new DataChunk(this.curByteIndex, this.sizeOfChunk);
                bytesRead = this.inputStream.read(currDataChunk.getData());
            }

        }catch (IOException e){
            System.err.println(RuntimeMessages.FAILED_TO_FETCH_DATA_FROM_SERVER);
        }

        // when finished, put a dummy chunk in the queue.
        // *** in future development this line will be execute be the Manager.
        writeToQueue(new DataChunk(-1,0));

        try {
            this.inputStream.close();
        }catch(IOException e){
            System.err.println(RuntimeMessages.INPUTSTREAM_CLOSE_EXEPTION);
        }
    }

    private void writeToQueue(DataChunk dataChunk){
        try{
            this.queue.put(dataChunk);
          }catch (Exception e){
            System.err.println(RuntimeMessages.FAILED_TO_INSERT_INTO_THE_QUEUE);
        }
    }
}
