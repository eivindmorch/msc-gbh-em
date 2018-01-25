package core.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static core.SystemSettings.RESOURCES_FILE_PATH;


public class Writer {

    private BufferedWriter bufferedWriter;

    public Writer(String intraResourcesFolderPath, String fileName) {
        try {
            String absoluteFolderPath = RESOURCES_FILE_PATH + intraResourcesFolderPath;
            new File(absoluteFolderPath).mkdirs();

            bufferedWriter = new BufferedWriter(new FileWriter((absoluteFolderPath + fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(String line) {

        try {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
