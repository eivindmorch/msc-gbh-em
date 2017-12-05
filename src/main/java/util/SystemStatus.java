package util;

import settings.SystemSettings;

import java.util.Date;

public class SystemStatus {

    public static String startTime = SystemSettings.dateFormat.format(new Date());
    public static String currentScenario = "follow_time-contrained-earth.scnx"; // TODO

    public static int currentTrainingEpoch = 0;
    public static int currentTrainingScenario = 0;
    public static int currentTrainingChromosome = 0;


}
