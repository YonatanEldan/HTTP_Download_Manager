package modules;

import Constants.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ArrayBlockingQueue;

public class Writer implements Runnable{
    ArrayBlockingQueue<DataChunk> queue;
    RandomAccessFile file;
    boolean active = true;

    public Writer(ArrayBlockingQueue q, RandomAccessFile f){
        this.queue = q;
        this.file = f;
    }


    @Override
    public void run() {
        int i = 0;

        while(active){
            try {
                // get the dataChunk from the queue
                DataChunk dataChunk = queue.take();

                // detect the dummy node
                if(dataChunk.getFirstByteIndex() == -1){
                    finish();
                }
                else {
                    // write to the file
                    file.seek(dataChunk.getFirstByteIndex());
                    file.write(dataChunk.getData(), 0, dataChunk.getSize());

                    //test
                    System.out.println("data chunk was writen to file \n" +
                                        "offset: " + dataChunk.getFirstByteIndex() + "\n" +
                                        "size: " + dataChunk.getSize() + "\n");
                    i++;


                }

            } catch (InterruptedException e) {
                System.err.println(RuntimeMessages.FAILED_TO_TAKE_DATA_FROM_THE_QUEUE);
            } catch (IOException e) {
                System.err.println(RuntimeMessages.FAILED_WHEN_WRITING_TO_FILE);
            }
        }
    }

    public void finish(){
        active = false;
    }

}
