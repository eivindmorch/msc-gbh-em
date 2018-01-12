package core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static core.settings.SystemSettings.resourcesFilePath;


public class Reader {

    private BufferedReader bufferedReader;

    public Reader(String intraResourcesPath) {
        try {
            File dataFile = new File((resourcesFilePath + intraResourcesPath));
            bufferedReader = new BufferedReader(new FileReader(dataFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
