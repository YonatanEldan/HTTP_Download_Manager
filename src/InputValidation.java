import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

public  class InputValidation{
    public static String[] validate (String input){
    String[] servers = null;
    // To avoid spaces in the beggining of the url
    String str = input.replaceAll(" ","");
        if (isURL(str)) {
        servers = new String[1];
        servers[0] = str;

    } else {
        List<String> URLlist = new ArrayList<String>();

        try {
            FileInputStream fstream_school = new FileInputStream(input);
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
        return servers;

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
