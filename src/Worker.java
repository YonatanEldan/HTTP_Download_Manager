import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;

public class Worker implements Runnable {
    private String url;
    private long firstByteIndex, curByteIndex,lastByteIndex;
    private int sizeOfChunk;
    private ArrayBlockingQueue<DataChunk> queue;
    private Manager manager;
    ProgressKeeper progressKeeper;

    private InputStream inputStream;

    public Worker(long firstByteIndex, long lastByteIndex, String url, int sizeOfChunk, ArrayBlockingQueue<DataChunk> queue, Manager manager, ProgressKeeper progressKeeper){
        this.firstByteIndex = firstByteIndex;
        this.curByteIndex = firstByteIndex;
        this.lastByteIndex = lastByteIndex;
        this.url = url;
        this.sizeOfChunk = sizeOfChunk;
        this.queue = queue;
        this.manager = manager;
        this.progressKeeper = progressKeeper;
    }



    @Override
    public void run() {

        // TODO: check if the chunk is already written to the file.
        System.out.println("Start downloading range (" + this.firstByteIndex + " - " + this.lastByteIndex +") from:\n" + this.url);

        //skip the first interval of bytes that were already read before sending the get request.
        while(progressKeeper.isChunkSaved(this.curByteIndex)){
            this.curByteIndex += this.sizeOfChunk;
        }
        if (this.lastByteIndex < this.curByteIndex) return;

        //connect to server
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Range", "bytes=" + this.curByteIndex + "-" + this.lastByteIndex);
            connection.setConnectTimeout(ConfigurationsSettings.TIMEOUT_FOR_WORKER);
            connection.setReadTimeout(ConfigurationsSettings.TIMEOUT_FOR_WORKER);

            this.inputStream = connection.getInputStream();

        } catch(IOException e){
            manager.programShutDown(RuntimeMessages.SERVER_CONNECTION_FAILED + ":" + "\n " +
                                "server name: " + this.url + "\n" +
                                "cause: " + e.getMessage());
        }

        // read chunks and write to queue
        try {
            DataChunk currDataChunk;
            int bytesRead = 0;
            while (bytesRead != -1 && this.curByteIndex < this.lastByteIndex) {
                bytesRead = 0;
                if(progressKeeper.isChunkSaved(this.curByteIndex)){
                    this.inputStream.skip(this.sizeOfChunk);
                    this.curByteIndex += this.sizeOfChunk;
                    continue;
                }
                currDataChunk = new DataChunk(this.curByteIndex, this.sizeOfChunk);
               //fill all the dataChunk (except maybe for the last chunk of the file).
               while(bytesRead < this.sizeOfChunk){
                   int temp = this.inputStream.read(currDataChunk.getData(), bytesRead, this.sizeOfChunk-bytesRead);

                   // break if you got to the end of the stream.
                   if(temp == -1){
                       currDataChunk.setSize(bytesRead);
                       bytesRead = -1;
                       break;
                   } else{
                       bytesRead += temp;
                   }
               }

                writeToQueue(currDataChunk);
                this.curByteIndex += this.sizeOfChunk;
            }

        }catch (IOException e){
            manager.programShutDown(RuntimeMessages.FAILED_TO_FETCH_DATA_FROM_SERVER);
        }

        finally {
            System.out.println("Finished downloading");
            try {
                this.inputStream.close();
            }catch(IOException e){
               //ignore
            }
        }
    }

    private void writeToQueue(DataChunk dataChunk){
        try{
            this.queue.put(dataChunk);
          }catch (Exception e){
            manager.programShutDown(RuntimeMessages.FAILED_TO_SAVE_DATA);
        }
    }
}
