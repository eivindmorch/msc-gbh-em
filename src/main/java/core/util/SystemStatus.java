package core.util;

import core.SystemSettings;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class SystemStatus {

    public static Date START_TIME = new Date();
    public static String START_TIME_STRING = SystemSettings.DATE_FORMAT.format(START_TIME);

    public static double getRuntimeInSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - START_TIME.getTime());
    }

}
