import java.io.*;
import java.nio.file.*;
import java.util.Arrays;

import modules.*;



public class Tests {

    public static void main(String[] arg){

        String downLoadedFilePath = "downloadedMario.avi";
        String originFilePath = "Mario1_500.avi";
        compareFiles(downLoadedFilePath, originFilePath);

    }

    public static void compareFiles(String file1Path, String file2Path){

        try {
            byte[] f1 = Files.readAllBytes(Paths.get(file1Path));
            byte[] f2 = Files.readAllBytes(Paths.get(file2Path));

            if(f1.length != f2.length){
                System.out.println("compare files test: the length of the array bytes is different");
                return;
            }

            // compare
            System.out.println("comparing files...");
            for (int i = 0; i < f1.length; i++) {
                if(f1[i] != f2[i]){
                    System.out.println("compare files test: the bytes at index " + i + " are different !");
                    return;
                }
            }

            System.out.println("compare files test: passed");

        } catch (IOException e) {
            System.err.println("failed to compare the files");
        }



    }
}
