import modules.Worker;
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
        ArrayBlockingQueue<Byte> queue = new ArrayBlockingQueue<Byte>(1000);
//        queue.put();
//        queue.poll();


        // init worker obj

        //Worker worker = new Worker();

       // connect to server

       //loop: read all the remote file by chunks.

    }





    }

