import java.net.*;
import  java.io.*;
import java.util.*;


public class IdcDm {

    public static void main(String[] args) throws IOException {
        //In case we don't get an input for max num threads
        int maxNumOfThreads = 1;

        if(args.length==2) {
            maxNumOfThreads = Integer.parseInt(args[1]);
        }
        String[] servers = InputValidation.validate(args[0]);

        //init manager
        Manager manager = new Manager(servers, maxNumOfThreads);
        String resultMessage = manager.execute();

        System.out.println(resultMessage);
    }

}
