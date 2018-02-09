package core.util;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class SystemUtil {

    public static Random random = new Random();

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
}
