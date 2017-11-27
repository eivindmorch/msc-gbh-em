package unit;

import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnitHandler {

    private static final Logger logger = LoggerFactory.getLogger(UnitHandler.class);


    private static HashMap<String, List<PhysicalEntityObject>> targetFollowerMap = new HashMap<>();
    private static HashMap<String, Unit> markingUnitMap = new HashMap<>();

    public static void addUnit(PhysicalEntityObject physicalEntity) {
        String marking = physicalEntity.getMarking().getMarking();
        ObjectInstanceHandle handle = physicalEntity.getObjectInstanceHandle();

        Unit unit;
        if (isTarget(marking)) {
            unit = new Unit(marking, handle);
        } else if (isFollower(marking)) {
            String targetName = getTargetNameFromFollowerMarking(marking);
            Unit targetUnit = markingUnitMap.get(targetName);
            if (targetUnit != null) {
                unit = new FollowerUnit(handle, marking, targetUnit);
            } else {
                // Schedules Follower for initialisation when Target is initialised
                targetFollowerMap.computeIfAbsent(targetName, k -> new ArrayList<>());
                targetFollowerMap.get(targetName).add(physicalEntity);
                logger.info("FollowerUnit " + marking + " was scheduled for later initialisation.");
                return;
            }
        } else {
            logger.warn("Unit " + marking + " was NOT added due to no related role.");
            return;
        }

        markingUnitMap.put(marking, unit);
        logger.info("Unit " + marking + " was added with handle " + handle + ".");
        UnitLogger.register(unit);

        // Initiates all scheduled Followers with this unit as Target
        if (targetFollowerMap.get(marking) != null) {
            targetFollowerMap.get(marking).forEach(UnitHandler::addUnit);
        }
    }

    public static void updateUnits(double timestamp) {
        for (Unit unit : markingUnitMap.values()) {
            unit.updateData(timestamp);
        }
    }

    private static boolean isFollower(String marking) {
        return marking.startsWith("F");
    }

    private static boolean isTarget(String marking) {
        return marking.startsWith("T");
    }

    private static String getTargetNameFromFollowerMarking(String marking) {
        return marking.substring(marking.indexOf('-') + 1, marking.length());
    }

    public static int getNumOfUnits() {
        return markingUnitMap.size();
    }

    public static void reset() {
        markingUnitMap = new HashMap<>();
    }
}
