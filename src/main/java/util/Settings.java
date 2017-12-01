package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@SuppressWarnings("WeakerAccess")
public class Settings {

    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss");

    public static String filePathRoot = System.getProperty("user.dir") + "/src/main/resources/";

    public static String exampleDataFilePath = "follower/0.csv";
    public static String iterationDataFilePath = "follower/1.csv";

    public static String[] examples = new String[] {"2", "4"};


}
