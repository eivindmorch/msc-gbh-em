package util;

import java.util.Date;

public class SystemStatus {

    public static String startTime = Settings.dateFormat.format(new Date());
    public static String currentScenario = ""; // TODO

    public static int currentTrainingEpoch = 0;
    public static int currentTrainingScenario = 0;
    public static int currentTrainingChromosome = 0;


}
