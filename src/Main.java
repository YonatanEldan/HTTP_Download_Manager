import modules.*;
import modules.Writer;

import java.net.*;
import  java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Main {

    public static void main(String[] args) throws IOException {
        String[] servers = null;

        if (isURL(args[0])) {
            servers[0] = args[0];
        } else {
            servers = null;
            List<String> itemsSchool = new ArrayList<String>();

            try {
                FileInputStream fstream_school = new FileInputStream(args[0]);
                DataInputStream data_input = new DataInputStream(fstream_school);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input));
                String str_line;

                while ((str_line = buffer.readLine()) != null) {
                    str_line = str_line.trim();
                    if ((str_line.length() != 0)) {
                        itemsSchool.add(str_line);
                    }
                }

                servers = (String[]) itemsSchool.toArray(new String[itemsSchool.size()]);
            } catch (Exception E) {
                E.printStackTrace();
            }


            //init manager
            Manager manager = new Manager(servers, 2);
            manager.run();


//        // init blocking queue
//        ArrayBlockingQueue<DataChunk> queue = new ArrayBlockingQueue<>(1000);
//
//        // init and start the worker.
//        String url = "https://ia800303.us.archive.org/19/items/Mario1_500/Mario1_500.avi";
//        Worker worker = new Worker(0, 24334492 - 1, url, 4096, queue);
//        Thread T = new Thread(worker);
//        T.start();
//        System.out.println("Started the worker thread");
//
//        //init and start the writer.
//        RandomAccessFile raf = new RandomAccessFile("downloadedMario.avi", "rw");
//        Writer writer = new Writer(queue, raf);
//        writer.run();
//        raf.close();

            System.out.println("Program finished successfully!");
        }
    }

    public static boolean isURL(String url){
        try {
            new URL(url);
            return true;
        } catch (Exception e){
            return false;
        }

    }
}

