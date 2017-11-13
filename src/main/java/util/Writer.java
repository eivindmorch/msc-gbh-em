package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static util.Values.filePathRoot;


public class Writer {

    private BufferedWriter bufferedWriter;

    public Writer(String resPath, String fileCsvHeader) {
        try {
            String filePath = filePathRoot + resPath;
            new File(filePath).mkdirs();

            long fileCount = Files.list(Paths.get(filePath)).count();
            String filename = fileCount + ".csv";

            bufferedWriter = new BufferedWriter(new FileWriter((filePath + filename)));
            writeLine(fileCsvHeader);

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
