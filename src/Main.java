import java.net.*;
import  java.io.*;
import java.util.*;


public class Main {

    public static void main(String[] args) throws IOException {

        String[] servers = null;
        // In case we don't get an input for maxnumthreads
        int maxNumOfThreads = 1;
        // To avoid spaces in the beggining of the url
        String str = args[0].replaceAll(" ","");
        //String str = new String(args[0]);
        if (isURL(args[0])) {
            System.out.println(args[0]);
            servers = new String[1];
            servers[0] = str;
            System.out.println(servers[0]);

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
            } catch (Exception E) {
                //E.printStackTrace();
            }
        }
        if(args.length==2) {
            maxNumOfThreads = Integer.parseInt(args[1]);
            System.out.println("Downloading using " + maxNumOfThreads + " connections...");
        }
        else{
            System.out.println("Downloading...");
        }

        //init manager
        Manager manager = new Manager(servers, maxNumOfThreads);
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
