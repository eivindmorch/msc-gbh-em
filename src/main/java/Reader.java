import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class Reader {

    private BufferedReader bufferedReader;
    private String filePathRoot = System.getProperty("user.dir") + "/src/main/resources/";
    public final List<String> fileHeader;

    Reader(String resPath, String filename) {
        String filePath = filePathRoot + resPath;
        try {
            File dataFile = new File((filePath + filename));
            bufferedReader = new BufferedReader(new FileReader(dataFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileHeader = readLine();
    }

    List<String> readLine() {
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

    public static void main(String[] args) {
        Reader reader = new Reader("raw/follower/", "0.csv");
        List<String> line = reader.fileHeader;
        while(line != null) {
            System.out.println(line);
            line = reader.readLine();
        }
    }

}
