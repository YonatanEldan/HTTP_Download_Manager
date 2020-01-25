import java.io.*;
import java.nio.file.*;
import java.util.Arrays;

import modules.*;

import javax.print.DocFlavor;


public class Tests {

    public static void main(String[] arg){

        //test the mario files
        String downLoadedFilePath = "downloadedMario.avi";
        String originFilePath = "Mario1_500.avi";
        //compareFiles(downLoadedFilePath, originFilePath);

        //test the iso files
        String origin = "CentOS-6.10-x86_64-netinstall.iso";
        String downloaded = "CentOS-6.10-x86_64-netinstall-downloaded.iso";
        compareFiles(downloaded, origin);

    }

    public static void compareFiles(String file1Path, String file2Path){

        try {
            byte[] f1 = Files.readAllBytes(Paths.get(file1Path));
            byte[] f2 = Files.readAllBytes(Paths.get(file2Path));

            if(f1.length != f2.length){
                System.out.println("compare files test: the length of the array bytes is different\n" +
                                    "downloaded (1st func arg) length is: " + f1.length + "\n" +
                                    "origin (2nd func arg) length is: " + f2.length);
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
