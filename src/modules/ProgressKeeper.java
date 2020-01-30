package modules;

import Constants.ConfigurationsSettings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;

public class ProgressKeeper {
    private HashSet<Long> savedChunks;
    private String targetFileName;

    private File mapMetaDataFile = new File("mapMetaD.ser");
    private File fileNameMetaDataFile = new File("nameMetaD.ser");
    private File temp1MetaDataFile = new File("temp1MetaD.ser");
    private File temp2MetaDataFile = new File("temp2MetaD.ser");

    private final int SIZE_OF_DATACHUNK = ConfigurationsSettings.SIZE_OF_DATACHUNK;
    private long targetFileSize;
    private int currProgress = 0;


    // NOTE:
    public ProgressKeeper(String fileName, long targetFileSize){
        this.targetFileName = fileName;
        this.targetFileSize = targetFileSize;

        // if the both the target file and the meta datafile are already exists, init form file.
        if(isInProgress()) {
            savedChunks = (HashSet) readMetaDataFile(mapMetaDataFile);
        }
        else{
            try {
                // create the metaData files
              mapMetaDataFile.createNewFile();
              fileNameMetaDataFile.createNewFile();

            } catch (IOException e){
                System.err.println(e.getMessage());
                e.printStackTrace();
            }

            this.savedChunks = new HashSet<>();
            writeMetaDataFile(mapMetaDataFile, temp1MetaDataFile, savedChunks);
            writeMetaDataFile(fileNameMetaDataFile, temp2MetaDataFile, targetFileName);

        }
    }

    private boolean isInProgress(){

        //if all of the required files exists, check if the meta data belongs to the current target file.
        if(mapMetaDataFile.exists() && fileNameMetaDataFile.exists() && new File(targetFileName).exists()){
            String metaDataName = (String) readMetaDataFile(fileNameMetaDataFile);
            return targetFileName.equals(metaDataName);
        }

        return false;
    }

    private void save(){
        writeMetaDataFile(mapMetaDataFile, temp1MetaDataFile, savedChunks);
    }

    private void writeMetaDataFile(File metaDataFile, File tempMetaDataFile, Object obj){
        try {

            // first, write to the temp meta data file
            FileOutputStream fos = new FileOutputStream(tempMetaDataFile.getName());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.close();
            fos.close();

            // then, swap the content to the real meta data file
            Files.move(tempMetaDataFile.toPath(), metaDataFile.toPath(), StandardCopyOption.ATOMIC_MOVE);

        } catch (IOException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private Object readMetaDataFile(File metaDataFile) {

        try {
            FileInputStream fis = new FileInputStream(metaDataFile.getName());
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            ois.close();
            fis.close();

            return obj;
        } catch(IOException | ClassNotFoundException e){
            System.err.println(e.getMessage());
            System.err.println("could not read the meta data file. a new object was returned instead");
            e.printStackTrace();
        }

        return new Object();
    }

    public void delete(){
        this.mapMetaDataFile.delete();
        this.fileNameMetaDataFile.delete();
    }

    public void addSavedChunk(long chunkId){
        savedChunks.add(chunkId);
        save();
        printProgress();
    }

    public boolean isChunkSaved(long chunkId){
        return savedChunks.contains(chunkId);
    }

    private int calcProgress(){
        double downloaded = (double) (savedChunks.size() * SIZE_OF_DATACHUNK) / targetFileSize;
        return (int) (downloaded * 100);
    }

    private void printProgress(){
        int progress = calcProgress();
        if (currProgress < progress){
            currProgress = progress;
            System.out.println("Downloaded " + currProgress + "% ...");
        }
    }


    @Override
    public String toString() {
        return "ProgressKeeper{\n" +
                "savedChunks=" + savedChunks +
                "\n}";
    }
}
