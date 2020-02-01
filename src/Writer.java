import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Writer implements Runnable{
    Manager manager;
    ProgressKeeper progressKeeper;
    ArrayBlockingQueue<DataChunk> queue;
    RandomAccessFile file;
    String targetFileName;
    boolean active = true;

    public Writer(ArrayBlockingQueue q, String fileName, Manager manager, ProgressKeeper progressKeeper){
        this.queue = q;
        this.targetFileName = fileName;
        this.manager = manager;
        this.progressKeeper = progressKeeper;
    }


    @Override
    public void run() {

        try {
            this.file = new RandomAccessFile(this.targetFileName, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(active){
            try {
                // get the dataChunk from the queue
                DataChunk dataChunk = queue.poll(ConfigurationsSettings.TIMEOUT_FOR_WRITER, TimeUnit.MILLISECONDS);
                // detect the dummy node
                if (dataChunk == null || dataChunk.getFirstByteIndex() == -1) {
                    finish();
                } else {
                    // write to the file
                    file.seek(dataChunk.getFirstByteIndex());
                    file.write(dataChunk.getData(), 0, dataChunk.getSize());

                    //update the metadata
                    progressKeeper.addSavedChunk(dataChunk.getFirstByteIndex());
                }

            }catch(InterruptedException e){
                //ignore
            } catch (IOException e) {
                manager.programShutDown(RuntimeMessages.FAILED_WHEN_WRITING_TO_FILE);
            }
        }

        try {
            this.file.close();
        } catch (IOException e) {
            //ignore
        }
    }

    public void finish(){
        active = false;
    }

}
