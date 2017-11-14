package util;

@SuppressWarnings("WeakerAccess")
public class Values {

    public static String filePathRoot = System.getProperty("user.dir") + "/src/main/resources/";
    public static String processedDataPath = "data/processed/";
    public static String rawDataPath = "data/raw/";

    public static String rawDataHeader = "timestamp, posX, posY, posZ, velX, velY, velZ";
    public static String processedDataHeader = "timestamp, distance, angle";

    public static String exampleDataFilePath = processedDataPath + "follower/0.csv";
    public static String iterationDataFilePath = processedDataPath + "follower/1.csv";

    public enum Role {
        FOLLOWER, TARGET
    }
}
