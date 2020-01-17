import modules.Worker;
import java.net.*;
import  java.io.*;
import java.util.*;


public class Main {

    public static void main(String[] args) throws IOException{

       // get input from the user
        for (String s: args) {
            System.out.println(s);
        }


        // init worker obj
        //Worker worker = new Worker();
       // connect to server

       //loop: read all the remote file by chunks.

        URL url = new URL("http://centos.activecloud.co.il");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Range", "bytes=0-1023");
        con.setRequestProperty("Host", "6.10/isos/x86_64/CentOS-6.10-x86_64-netinstall.iso");
        System.out.println(con.getResponseCode());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();





    }
}
