package core.util;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static core.SystemSettings.RESOURCES_FILE_PATH;


public class Reader {

    private BufferedReader bufferedReader;

    public Reader(String intraResourcesFilePath) throws FileNotFoundException {
        File dataFile = new File((RESOURCES_FILE_PATH + intraResourcesFilePath));
        bufferedReader = new BufferedReader(new FileReader(dataFile));
    }

    public String readLine() {
        try {
            String line = bufferedReader.readLine();
            if (line != null) {
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> stringToCsvList(String str) {
        return Arrays.asList(str.split("\\s*,\\s*"));
    }

}
