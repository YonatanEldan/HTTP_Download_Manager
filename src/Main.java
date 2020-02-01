import java.net.*;
import  java.io.*;
import java.util.*;


public class Main {

    public static void main(String[] args) throws IOException {

        String[] servers = null;
        // We will define the maximum numer of threads to be the file size / 25000
        int maxNumOfThreads = 4;
        // To avoid spaces in the beginning of the url
        String str = args[0].replaceAll("\\s+","");
        if (isURL(str)) {
            System.out.println(str);
            servers = new String[]{str};

        } else {
            List<String> URLlist = new ArrayList<String>();

            try {
                FileInputStream fstream_school = new FileInputStream(args[0]);
                DataInputStream data_input = new DataInputStream(fstream_school);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input));
                String str_line;

                while ((str_line = buffer.readLine()) != null) {
                    str_line = str_line.trim();
                    if ((str_line.length() != 0)) {
                        URLlist.add(str_line);

                    }
                }

                servers = URLlist.toArray(new String[URLlist.size()]);
                if(args.length==2) {
                    maxNumOfThreads = Integer.parseInt(args[1]);
                    System.out.println("Downloading using " + maxNumOfThreads + " connections...");
                }
                else{
                    System.out.println("Downloading...");
                }
            } catch (Exception E) {
                //E.printStackTrace();
            }
        }

        //init manager
        servers = new String[]{"https://archive.org/download/Mario1_500/Mario1_500.avi"};
        Manager manager = new Manager(servers, Integer.parseInt(args[1]));
        String resultMessage = manager.execute();

        System.out.println(resultMessage);


        // run the tests and delete the new file
        Tests.main(new String[]{});
        System.out.println("deleting the new file and exit the program...");

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
