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
    long firstByteIndex, lastByteIndex;
    BufferedReader reader = null;
    PrintWriter writer = null;
    int sizeOfChunk;
    ArrayBlockingQueue<DataChunk> queue;

    public Worker(long firstByteIndex, long lastByteIndex, String url, int sizeOfChunk, ArrayBlockingQueue<DataChunk> queue){
        this.firstByteIndex = firstByteIndex;
        this.lastByteIndex = lastByteIndex;
        this.url = url;
        this.sizeOfChunk = sizeOfChunk;
        this.queue = queue;
    }

    @Override
    public void run() {
        while(this.firstByteIndex + this.sizeOfChunk < this.lastByteIndex) {
            readFromServer();
            writeToQueue();
            this.firstByteIndex += this.sizeOfChunk;
        }


    }

    private void connect(){

    }

    private void readFromServer(){
        try {
            URL url = new URL(this.url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            //
            con.setRequestProperty("Range", "bytes="+ dataChunk.getFirstByteIndex()+"-" + dataChunk.getlastByteIndex());
            InputStream inputStream = con.getInputStream();
            byte[] buffer = new byte[this.dataChunk.size];
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
      //  this.dataChunk.data = [];

    }
}
