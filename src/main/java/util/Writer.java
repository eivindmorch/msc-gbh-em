package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static settings.SystemSettings.resourcesFilePath;


public class Writer {

    private BufferedWriter bufferedWriter;

    public Writer(String intraResourcesFolderPath, String fileName) {
        try {
            String absoluteFolderPath = resourcesFilePath + intraResourcesFolderPath;
            new File(absoluteFolderPath).mkdirs();

            bufferedWriter = new BufferedWriter(new FileWriter((absoluteFolderPath + "/" + fileName)));
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
