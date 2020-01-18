import modules.*;
import modules.Writer;

import java.net.*;
import  java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Main {

    public static void main(String[] args) throws IOException{

       // get input from the user
        for (String s: args) {
            System.out.println(s);
        }

        // init blocking queue
        ArrayBlockingQueue<DataChunk> queue = new ArrayBlockingQueue<>(1000);


        // init worker obj
        String url = "http://centos.activecloud.co.il/6.10/isos/x86_64/CentOS-6.10-x86_64-netinstall.iso";
        Worker worker = new Worker(0, 240123904, url, 409600, queue);
        Thread T = new Thread(worker);
        T.run();

        //init writer obj
        RandomAccessFile raf = new RandomAccessFile("NewFileJava.iso", "rw");
        Writer writer = new Writer(queue, raf);
        write.run();
        raf.close();


        System.out.println("the size of the queue is : " + queue.size());

    }
}
