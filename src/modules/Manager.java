package modules;

import Constants.RuntimeMessages;
import java.io.IOException;

import java.io.RandomAccessFile;
import java.util.concurrent.ArrayBlockingQueue;
import java.net.HttpURLConnection;
import java.net.URL;

public class Manager implements Runnable{
    String[] servers;
    int MAX_THREADS_NUM;
    int fileSize = 0;

    // init blocking queue
    ArrayBlockingQueue<DataChunk> queue = new ArrayBlockingQueue<>(1000);

    public Manager(String[] servers, int maxThreadNum){
        this.servers = servers;
        this.MAX_THREADS_NUM = maxThreadNum;
    }

    @Override
    public void run() {

        //work
        int halfPoint = this.fileSize / 2;

        Worker worker1 = new Worker(0, halfPoint, this.servers[0], 4096, queue);
        Thread w1 = new Thread(worker1);

        Worker worker2 = new Worker(halfPoint + 1, fileSize - 1 , this.servers[0], 4096, queue);
        Thread w2 = new Thread(worker2);

        w1.start();
        w2.start();
        System.out.println("Started the workers threads");


        //init and start the writer.
        try {
            RandomAccessFile raf = new RandomAccessFile("downloadedMario.avi", "rw");
            Writer writer = new Writer(queue, raf);
            writer.run();
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // wait for the workers

        //insert dummy
        // when finished, put a dummy chunk in the queue.
        // *** in future development this line will be execute be the Manager.
       writeToQueue(new DataChunk(-1, 0));

    }

    private void writeToQueue(DataChunk dataChunk){
        try{
            this.queue.put(dataChunk);
        }catch (Exception e){
            System.err.println(RuntimeMessages.FAILED_TO_INSERT_INTO_THE_QUEUE);
        }
    }

    public static long getFileInfo(String URL) {
        try {
            URL Url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            return conn.getContentLengthLong();

        } catch (IOException e) {
            System.err.println(RuntimeMessages.SERVER_CONNECTION_FAILED);
        }
        return -1;
    }




}
