import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


class Writer {

    private BufferedWriter bufferedWriter;
    private String filePathRoot = System.getProperty("user.dir") + "/src/main/resources/";

    Writer(String resFolder, String fileCsvHeader) {
        try {
            String filePath = filePathRoot + resFolder +"/";
            long fileCount = Files.list(Paths.get(filePath)).count();
            String filename = fileCount + ".csv";

            writeLine(fileCsvHeader);

            bufferedWriter = new BufferedWriter(new FileWriter((filePath + filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(String line) {
        try {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
