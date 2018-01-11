package util;

import settings.SystemSettings;
import java.util.Date;

public abstract class SystemStatus {

    public static String startTime = SystemSettings.dateFormat.format(new Date());
    public static String currentScenario;
    public static SystemMode systemMode = SystemMode.UNSPECIFIED; // Overwritten by main classes

    public static int currentTrainingEpoch = 0;
    public static int currentTrainingScenario = 0;
    public static int currentTrainingChromosome = 0;

}
