package modules;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Worker implements Runnable {
    DataChunk dataChunk;
    String url;
    long firstByteIndex, curByteIndex,lastByteIndex;
    BufferedReader reader = null;
    PrintWriter writer = null;
    int sizeOfChunk;
    ArrayBlockingQueue<DataChunk> queue;
    //Testing paramaters
    int iteration = 1;

    public Worker(long firstByteIndex, long lastByteIndex, String url, int sizeOfChunk, ArrayBlockingQueue<DataChunk> queue){
        this.firstByteIndex = firstByteIndex;
        this.curByteIndex = firstByteIndex;
        this.lastByteIndex = lastByteIndex;
        this.url = url;
        this.sizeOfChunk = sizeOfChunk;
        this.queue = queue;
        this.dataChunk = new DataChunk(this.curByteIndex, this.curByteIndex + this.sizeOfChunk);
    }

    @Override
    public void run() {
        while(this.curByteIndex < this.lastByteIndex) {
            //System.out.println("\n number of iteration " + iteration + "\n curByteIndex: " + this.curByteIndex + "\n lastByteIndex: " + this.lastByteIndex );
            // checking if we are at the last packet, might have a different size.
            if(this.curByteIndex + this.sizeOfChunk < this.lastByteIndex) {
                this.dataChunk = new DataChunk(this.curByteIndex, this.curByteIndex + this.sizeOfChunk);
            }
            else{
                this.dataChunk = new DataChunk(this.curByteIndex, this.lastByteIndex);
            }
            System.out.println("Current first byte" + this.curByteIndex);
            readFromServer();
            writeToQueue();
            this.curByteIndex += this.sizeOfChunk;
            // for testing purposes
            iteration++;
        }
        this.dataChunk = new DataChunk(-1,-1);
        writeToQueue();
    }


    private void readFromServer(){
        try {
            URL url = new URL(this.url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            // check if the get returns the lastByteIndex
            con.setRequestProperty("Range", "bytes="+ dataChunk.getFirstByteIndex()+"-" + dataChunk.getlastByteIndex());
            InputStream inputStream = con.getInputStream();
            byte[] buffer = new byte[this.dataChunk.size];
            //System.out.println("\n http status: " + con.getResponseCode());
            // read stream data into buffer
            inputStream.read(buffer);
            this.dataChunk.data = buffer;
            inputStream.close();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("error in readFromServer");
        }
    }

    private void writeToQueue(){
        try{
            this.queue.put(this.dataChunk);
          }catch (Exception e){
            e.printStackTrace();
            System.out.println("error in writeToQueue");
        }

    }
}
