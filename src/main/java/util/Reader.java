package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static util.Values.filePathRoot;


public class Reader {

    private BufferedReader bufferedReader;
    public final List<String> fileHeader;

    public Reader(String filePath) {
        try {
            File dataFile = new File((filePathRoot + filePath));
            bufferedReader = new BufferedReader(new FileReader(dataFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileHeader = readLine();
    }

    public List<String> readLine() {
        try {
            String line = bufferedReader.readLine();
            if (line != null) {
                return Arrays.asList(line.split("\\s*,\\s*"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}