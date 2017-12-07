package util;

import model.btree.GenBehaviorTree;
import settings.SystemSettings;
import unit.Unit;

import java.util.Date;
import java.util.HashMap;

public abstract class SystemStatus {

    public static String startTime = SystemSettings.dateFormat.format(new Date());
    public static String currentScenario = "follow_time-contrained-earth.scnx"; // TODO
    public static SystemMode systemMode;

    public static int currentTrainingEpoch = 0;
    public static int currentTrainingScenario = 0;
    public static int currentTrainingChromosome = 0;

    // TODO Move?
    public static HashMap<Class<? extends Unit>, GenBehaviorTree> controlledUnitBtreeMap = new HashMap<>();


}
