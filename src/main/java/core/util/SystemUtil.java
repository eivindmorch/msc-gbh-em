package core.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class SystemUtil {

    public static void sleepMilliseconds(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String commandOptionsListToString(List<String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < params.size() - 1; i++) {
            stringBuilder.append(params.get(i));
            stringBuilder.append(" ");
        }
        stringBuilder.append(params.get(params.size() - 1));
        return stringBuilder.toString();
    }

    public static String getDataFileIntraResourcesFolderPath(int epoch, int example, int chromosome) {
        StringBuilder stringBuilder = new StringBuilder("data/");
        stringBuilder.append(SystemStatus.systemMode.name().toLowerCase()).append("/");
        stringBuilder.append(SystemStatus.startTime).append("/");
        if (SystemStatus.systemMode == SystemMode.TRAINING) {
            stringBuilder
                    .append("epoch").append(epoch).append("/")
                    .append("example").append(example).append("/")
                    .append("chromosome").append(chromosome).append("/");
        }
        return stringBuilder.toString();
    }
}
