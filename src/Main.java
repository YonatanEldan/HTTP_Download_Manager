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
        String url = "https://ia800303.us.archive.org/19/items/Mario1_500/Mario1_500.avi";
        Worker worker = new Worker(0, 24334492 - 1, url, 4096, queue);
        Thread T = new Thread(worker);
        T.start();
        System.out.println("Started the worker thread");


        //init writer obj
        RandomAccessFile raf = new RandomAccessFile("TestFileJava.iso", "rw");
        Writer writer = new Writer(queue, raf);
        writer.run();
        raf.close();

        System.out.println("finished the program successfully!");
    }




}
