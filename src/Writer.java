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
                if(dataChunk == null || dataChunk.getFirstByteIndex() == -1){
                    finish();
                }
                else {
                    // write to the file
                    file.seek(dataChunk.getFirstByteIndex());
                    file.write(dataChunk.getData(), 0, dataChunk.getSize());

                    //TODO: update the metadata
                    progressKeeper.addSavedChunk(dataChunk.getFirstByteIndex());

//                    //test
//                    System.out.println("data chunk was writen to file \n" +
//                                        "offset: " + dataChunk.getFirstByteIndex() + "\n" +
//                                        "size: " + dataChunk.getSize() + "\n");
                }

            } catch (InterruptedException e) {
                System.err.println(RuntimeMessages.FAILED_TO_TAKE_DATA_FROM_THE_QUEUE);
            } catch (IOException e) {
                System.err.println(RuntimeMessages.FAILED_WHEN_WRITING_TO_FILE);
            }
        }


        try {
            this.file.close();
        } catch (IOException e) {
            System.err.println("failed closing the target file!!!");
            e.printStackTrace();
        }
    }

    public void finish(){
        active = false;
    }

}
