import modules.*;
import modules.Writer;

import java.net.*;
import  java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Main {

    public static void main(String[] args) throws IOException {
        String[] servers = null;
        int maxNumOfThreads = 0;

        if (isURL(args[0])) {
            servers = new String[]{"https://archive.org/download/Mario1_500/Mario1_500.avi"}; // testing:  change it later !!!!!!!
        } else {
            List<String> itemsSchool = new ArrayList<String>();

            try {
                FileInputStream fstream_school = new FileInputStream(args[0]);
                DataInputStream data_input = new DataInputStream(fstream_school);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input));
                String str_line;

                while ((str_line = buffer.readLine()) != null) {
                    str_line = str_line.trim();
                    if ((str_line.length() != 0)) {
                        itemsSchool.add(str_line);
                    }
                }

                servers = itemsSchool.toArray(new String[itemsSchool.size()]);
            } catch (Exception E) {
                E.printStackTrace();
            }
        }

        maxNumOfThreads = 16;

        //init manager
        Manager manager = new Manager(servers, maxNumOfThreads);
        manager.run();


        System.out.println("Download finished !");

        // run the tests and delete the new file
        Tests.main(new String[]{});
        System.out.println("deleting the new file and exit the program...");
//        File res = new File("CentOS-6.10-x86_64-netinstall-downloaded.iso");
//        res.delete();
//
//        File res2 = new File("downloadedMario.avi");
//        res2.delete();

    }

    public static boolean isURL(String url){
        try {
            new URL(url);
            return true;
        } catch (Exception e){
            return false;
        }

    }
}

