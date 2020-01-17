package modules;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class Worker implements Runnable {
    DataChunk dataChunk;
    String url;
    long firstByteIndex, lastByteIndex;
    BufferedReader reader = null;
    PrintWriter writer = null;

    public Worker(long firstByteIndex, long lastByteIndex, String url){
        this.firstByteIndex = firstByteIndex;
        this.lastByteIndex = lastByteIndex;
        this.url = url;
    }

    @Override
    public void run() {

    }

    private void connect(){

    }

    private void readFromServer(){

    }

    private void writeToQueue(BlockingQueue queue){

    }
}
