package modules;

import jdk.jfr.DataAmount;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ArrayBlockingQueue;

public class Writer implements Runnable{
    ArrayBlockingQueue<DataChunk> queue;
    RandomAccessFile file;
    DataChunk dataChunk;

    public Writer(ArrayBlockingQueue q, RandomAccessFile f){
        this.queue = q;
        this.file = f;
    }


    @Override
    public void run() {

        while(true){
            try {
                dataChunk = queue.take();
                file.seek(dataChunk.getFirstByteIndex());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
