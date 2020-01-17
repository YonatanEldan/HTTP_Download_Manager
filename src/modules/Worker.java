package modules;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class Worker implements Runnable {
    DataChunk dataChunk;
    Socket socket = null;
    BufferedReader reader = null;
    PrintWriter writer = null;

    public Worker(long firstByteIndex, long lastByteIndex, String url){
        connect();
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
