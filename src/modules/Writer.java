package modules;

import jdk.jfr.DataAmount;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ArrayBlockingQueue;

public class Writer implements Runnable{
    ArrayBlockingQueue<DataChunk> queue;
    RandomAccessFile file;
    DataChunk dataChunk;
    boolean active = true;

    public Writer(ArrayBlockingQueue q, RandomAccessFile f){
        this.queue = q;
        this.file = f;
    }


    @Override
    public void run() {

        while(active){
            try {
                // get the dataChunk from the queue
                dataChunk = queue.take();

                // write to the file
                file.seek(dataChunk.getFirstByteIndex());
                file.write(dataChunk.data);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void finish(){
        active = false;
    }

}
