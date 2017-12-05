package util;

import java.util.concurrent.TimeUnit;

public abstract class SystemUtil {

    public static void sleepMilliseconds(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
