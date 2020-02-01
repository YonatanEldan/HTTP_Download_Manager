import java.io.IOException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.net.HttpURLConnection;
import java.net.URL;



public class Manager {

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

        // by default assign one worker per server.
        this.NUM_OF_WORKING_THREADS = maxThreadNum;

        getFileInfo(this.servers[0]);

        this.progressKeeper = new ProgressKeeper(targetFilename, fileSize);
    }

    public String execute() {
        //validate the file size
        if(this.fileSize <= 0){
            return RuntimeMessages.SERVER_CONNECTION_FAILED;
        }


        List<Thread> workerThreads = initWorkerThreads();

        //start the workers
        for (Thread thread : workerThreads) {
            thread.start();
        }


        //init and start the writer.
        Writer writer = new Writer(queue, targetFilename, this, this.progressKeeper);
        Thread writerThread = new Thread(writer);
        writerThread.start();


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
            progressKeeper.delete();
            return RuntimeMessages.THE_PROGRAM_SHUT_DOWN;
        }

        progressKeeper.delete();
        return RuntimeMessages.DOWNLOAD_SUCCEEDED;
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
                    worker = new Worker(currStart, currStart + numOfBytesPerWorker, servers[i], this.SIZE_OF_DATACHUNK, queue, this, this.progressKeeper);
                }

                workerThreads.add(new Thread(worker));
                currStart += numOfBytesPerWorker;
            }
        }

        return workerThreads;
    }

    private void writeToQueue(DataChunk dataChunk) throws InterruptedException {
            this.queue.put(dataChunk);
        }

    // maybe traverse on all the servers list and break when you get the data.
    private void getFileInfo(String URL) {
        try {
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");

            this.fileSize = conn.getContentLengthLong();
            int ThreadsUpperBound = (int)((this.fileSize) / ConfigurationsSettings.THREADS_SIZE_RATIO);
            if (this.NUM_OF_WORKING_THREADS > ThreadsUpperBound) {
                // We won't use more then 20 threads reagrless of the file size
                this.NUM_OF_WORKING_THREADS = Math.min(20, ThreadsUpperBound);
            }
            this.targetFilename = "Downloaded-" + URL.substring(URL.lastIndexOf('/')+1, URL.length() );
            // For printing purposes
            if(this.NUM_OF_WORKING_THREADS  > 1 ) System.out.println("Downloading using " + this.NUM_OF_WORKING_THREADS + " connections...");
            else System.out.println("Downloading...");
        } catch (IOException e) {
            System.err.println(RuntimeMessages.SERVER_CONNECTION_FAILED);
        }
    }


    private long calcNunOfChunksPerWorker(){
        long totalDataChunksNum = this.fileSize / this.SIZE_OF_DATACHUNK ;
        if((this.fileSize / this.SIZE_OF_DATACHUNK) % this.SIZE_OF_DATACHUNK != 0 ) totalDataChunksNum++;

        return  totalDataChunksNum / this.NUM_OF_WORKING_THREADS;
    }

    public void programShutDown(String msg){
        System.err.println(msg);
        System.exit(1);
    }
}

