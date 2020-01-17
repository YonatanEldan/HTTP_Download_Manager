package modules;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
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
        char c;
        int i=0;
        URL url = new URL("http://centos.activecloud.co.il/6.10/isos/x86_64/CentOS-6.10-x86_64-netinstall.iso");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Range", "bytes=0-1023");
        InputStream is = con.getInputStream();
        byte[] buffer = new byte[2048];
        // read stream data into buffer
        int data;
        while ((data = is.read()) != -1) {
            System.out.println(i);
            i++;
        }
        //System.out.println(i);
        is.close();

    }

    private void connect(){

    }

    private void readFromServer(){

    }

    private void writeToQueue(BlockingQueue queue){

    }
}
