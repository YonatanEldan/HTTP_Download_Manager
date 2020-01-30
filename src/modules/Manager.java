package modules;

import Constants.*;

import java.io.IOException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Manager implements Runnable {
    String[] servers;
    int NUM_OF_WORKING_THREADS;
    long fileSize = 0;
    String targetFilename;
    private final int SIZE_OF_DATACHUNK = ConfigurationsSettings.SIZE_OF_DATACHUNK;
    ProgressKeeper progressKeeper;

    // init blocking queue
    ArrayBlockingQueue<DataChunk> queue = new ArrayBlockingQueue<>(500);

    public Manager(String[] servers, int maxThreadNum) {
        this.servers = servers;

        // be default assign one worker per server.
        this.NUM_OF_WORKING_THREADS = maxThreadNum;

        getFileInfo(this.servers[0]);

        this.progressKeeper = new ProgressKeeper(targetFilename, fileSize);
    }

    @Override
    public void run() {

        List<Thread> workerThreads = initWorkerThreads();

        //start the workers
        for (Thread thread : workerThreads) {
            thread.start();
        }
        System.out.println("Started the workers threads");


        //init and start the writer.
        Writer writer = new Writer(queue, targetFilename, this, this.progressKeeper);
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
        finally {
            progressKeeper.delete();
        }
    }

    private List<Thread> initWorkerThreads() {
        List<Thread> workerThreads = new LinkedList<>();

        // get the number of connections per server
        int[] numOfConnections = new int[this.servers.length];
        for (int i = 0; i < this.NUM_OF_WORKING_THREADS; i++) {
            numOfConnections[i % servers.length]++;
        }

        // divide the total file size into parts in order to divide the work evenly between the workers.
        long numOfBytesPerWorker = this.SIZE_OF_DATACHUNK * calcNunOfChunksPerWorker();

        long currStart = 0;
        int workersCounter = 0;
        for (int i = 0; i < servers.length; i++) {
            for (int j = 0; j < numOfConnections[i]; j++) {
                Worker worker;
                workersCounter++;

                if (workersCounter == this.NUM_OF_WORKING_THREADS) {
                    // this is the last worker
                    worker = new Worker(currStart, this.fileSize - 1, servers[i], this.SIZE_OF_DATACHUNK, queue, this, this.progressKeeper);
                } else {
                    worker = new Worker(currStart, currStart + numOfBytesPerWorker, servers[i], 4096, queue, this, this.progressKeeper);
                }

                workerThreads.add(new Thread(worker));
                currStart += numOfBytesPerWorker;
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

    //TODO: handle to case where you cant connect to the given server.
    // maybe travers on all the servers list and break when you get the data.
    private void getFileInfo(String URL) {
        try {
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");

            this.fileSize = conn.getContentLengthLong();
            // TODO: get the correct file name...
//            this.targetFilename = extractFileName(this.servers[0]);
            this.targetFilename = "CentOS-6.10-x86_64-netinstall-downloaded.iso";

        } catch (IOException e) {
            System.err.println(RuntimeMessages.SERVER_CONNECTION_FAILED);
        }
    }

    private String extractFileName(String path){
        Pattern p = Pattern.compile(".*/(.+\\..+)$");
        Matcher m = p.matcher(path);

        String name  = m.group(1);
        return name;
    }

    private long calcNunOfChunksPerWorker(){
        long totalDataChunksNum = this.fileSize / this.SIZE_OF_DATACHUNK ;
        if((this.fileSize / this.SIZE_OF_DATACHUNK) % this.SIZE_OF_DATACHUNK != 0 ) totalDataChunksNum++;

        return  totalDataChunksNum / this.NUM_OF_WORKING_THREADS;
    }

}
