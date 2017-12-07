package util;

import settings.SystemSettings;

import java.util.Date;

public abstract class SystemStatus {

    public static String startTime = SystemSettings.dateFormat.format(new Date());
    public static String currentScenario = "follow_time-contrained-earth.scnx"; // TODO
    public static SystemMode systemMode;

    public static int currentTrainingEpoch = 0;
    public static int currentTrainingScenario = 0;
    public static int currentTrainingChromosome = 0;

    public static String getDataFileStorageFolder() {
        StringBuilder stringBuilder = new StringBuilder("data/");
        stringBuilder.append(systemMode.name().toLowerCase()).append("/");
        stringBuilder.append(SystemStatus.startTime).append("/");
        if (systemMode == SystemMode.TRAINING) {
            stringBuilder
                    .append("epoch").append(SystemStatus.currentTrainingEpoch).append("/")
                    .append("scenario").append(SystemStatus.currentTrainingScenario).append("/")
                    .append("chromosome").append(SystemStatus.currentTrainingChromosome).append("/");
        }
        return stringBuilder.toString();
    }
}
