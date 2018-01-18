package core.util;

import core.settings.SystemSettings;
import java.util.Date;

public abstract class SystemStatus {

    public static String startTime = SystemSettings.DATE_FORMAT.format(new Date());
    public static String currentScenario;
    public static SystemMode systemMode = SystemMode.UNSPECIFIED; // Overwritten by main classes

    public static int currentTrainingEpoch = 0;
    public static int currentTrainingExampleDataSetIndex = 0;
    public static int currentTrainingChromosome = 0;

}
