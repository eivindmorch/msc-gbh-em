package core.settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public abstract class SystemSettings {

    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss");
    public static final String resourcesFilePath = System.getProperty("user.dir") + "/src/main/resources/";

}
