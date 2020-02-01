import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;

public class ProgressKeeper {
    private boolean[] savedChunksArray;
    private int numOfSavedChunks = 0;
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

        // if both the target file and the meta datafile are already exists, init form file.
        if(isInProgress()) {
            savedChunksArray = (boolean[]) readMetaDataFile(mapMetaDataFile);
            retrieveNumOfSavedChunks();
        }
        else{
            try {
                // create the metaData files
              mapMetaDataFile.createNewFile();
              fileNameMetaDataFile.createNewFile();

            } catch (IOException e){
                System.err.println(RuntimeMessages.FAILED_INIT_META_DATA_FILES);
            }

            this.savedChunksArray = new boolean[(int) (targetFileSize / SIZE_OF_DATACHUNK) + 1];
            writeMetaDataFile(mapMetaDataFile, temp1MetaDataFile, savedChunksArray);
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
        writeMetaDataFile(mapMetaDataFile, temp1MetaDataFile, savedChunksArray);
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
            //ignore
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
            System.err.println(RuntimeMessages.COULD_NOT_READ_MATA_DATA);
        }

        return new Object();
    }

    public void delete(){
        this.mapMetaDataFile.delete();
        this.fileNameMetaDataFile.delete();
    }

    public void addSavedChunk(long chunkFirstByte){
        int index = (int) (chunkFirstByte/SIZE_OF_DATACHUNK);
        savedChunksArray[index] = true;
        numOfSavedChunks++;
        printProgress();
    }

    public boolean isChunkSaved(long chunkFirstByte){
        int index = (int) (chunkFirstByte / SIZE_OF_DATACHUNK);
        return savedChunksArray[index];
    }

    private int calcProgress(){
        double downloaded = (double) (numOfSavedChunks * SIZE_OF_DATACHUNK) / targetFileSize;
        return (int) (downloaded * 100);
    }

    private void printProgress(){
        int progress = calcProgress();
        if (currProgress < progress){
            save();
            currProgress = progress;
            System.out.println("Downloaded " + currProgress + "%");
        }
    }


    @Override
    public String toString() {
        return "ProgressKeeper{\n" +
                "savedChunksArray=" + arrayPrint(savedChunksArray) +
                "\n}";
    }

    private String arrayPrint(boolean[] arr){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arr.length ; i++) {
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    private void retrieveNumOfSavedChunks(){
        numOfSavedChunks = 0;
        for (int i = 0; i < savedChunksArray.length ; i++) {
            if(savedChunksArray[i]) numOfSavedChunks++;
        }
    }
}
