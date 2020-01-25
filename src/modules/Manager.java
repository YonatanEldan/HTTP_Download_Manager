package modules;

import Constants.RuntimeMessages;

import java.io.IOException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.net.HttpURLConnection;
import java.net.URL;

public class Manager implements Runnable {
    String[] servers;
    int NUM_OF_WORKING_THREADS;
    long fileSize = 0;

    // init blocking queue
    ArrayBlockingQueue<DataChunk> queue = new ArrayBlockingQueue<>(500);

    public Manager(String[] servers, int maxThreadNum) {
        this.servers = servers;

        // be default assign one worker per server.
        this.NUM_OF_WORKING_THREADS = maxThreadNum;
    }

    @Override
    public void run() {

        this.fileSize = getFileInfo(this.servers[0]);

        List<Thread> workerThreads = initWorkerThreads();

        //start the workers
        for (Thread thread : workerThreads) {
            thread.start();
        }
        System.out.println("Started the workers threads");


        //init and start the writer.
        Writer writer = new Writer(queue, "CentOS-6.10-x86_64-netinstall-downloaded.iso");
        Thread writerThread = new Thread(writer);
        writerThread.start();
        System.out.println("Started the writer thread");


        try {

            //join the workers
            for (Thread thread : workerThreads) {
                thread.join();
            }

            //insert dummy:
            // when finished, put a dummy chunk in the queue.
            writeToQueue(new DataChunk(-1, 0));

            writerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private List<Thread> initWorkerThreads() {
        List<Thread> workerThreads = new LinkedList<>();

        // divide the total file size into chunks in order to divide the work evenly between the workers.
        long workerChunk = this.fileSize / this.NUM_OF_WORKING_THREADS;

        // get the number of connections per server
        int[] numOfConnections = new int[this.servers.length];
        for (int i = 0; i < this.NUM_OF_WORKING_THREADS; i++) {
            numOfConnections[i % servers.length]++;
        }

        long currStart = 0;
        int workersCounter = 0;
        for (int i = 0; i < servers.length; i++) {
            for (int j = 0; j < numOfConnections[i]; j++) {

                Worker worker;
                workersCounter++;

                if (workersCounter == this.NUM_OF_WORKING_THREADS) {
                    // this is the last worker
                    worker = new Worker(currStart, this.fileSize - 1, servers[i], 4096, queue);

                } else {
                    worker = new Worker(currStart, currStart + workerChunk - 1, servers[i], 4096, queue);
                }

                workerThreads.add(new Thread(worker));
                currStart += workerChunk;
            }
        }

        return workerThreads;
    }

    private void writeToQueue(DataChunk dataChunk) {
        try {
            this.queue.put(dataChunk);
        } catch (Exception e) {
            System.err.println(RuntimeMessages.FAILED_TO_INSERT_INTO_THE_QUEUE);
        }
    }

    private static long getFileInfo(String URL) {
        try {
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            return conn.getContentLengthLong();

        } catch (IOException e) {
            System.err.println(RuntimeMessages.SERVER_CONNECTION_FAILED);
        }
        return -1;
    }


}
