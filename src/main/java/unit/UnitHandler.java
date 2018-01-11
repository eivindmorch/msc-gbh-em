package unit;

import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class UnitHandler {

    private static final Logger logger = LoggerFactory.getLogger(UnitHandler.class);


    private static HashMap<String, List<PhysicalEntityObject>> targetFollowerMap = new HashMap<>();
    private static HashMap<String, Unit> markingUnitMap = new HashMap<>();
    private static ArrayList<ControlledUnit> controlledUnits = new ArrayList<>();

    public static void addUnit(PhysicalEntityObject physicalEntity) {
        String marking = physicalEntity.getMarking().getMarking();
        ObjectInstanceHandle handle = physicalEntity.getObjectInstanceHandle();

        // TODO Move method to experiment-dependent package
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

        String unitName = getUnitName(marking);
        if (markingUnitMap.get(unitName) == null) {
            markingUnitMap.put(unitName, unit);
            logger.info("Unit added -- Marking: " + marking + ", Handle: " + handle);
            UnitLogger.register(unit);

            // Initiates all scheduled Followers with this unit as Target
            if (targetFollowerMap.get(unitName) != null) {
                targetFollowerMap.get(unitName).forEach(UnitHandler::addUnit);
            }

            // TODO Replace with "c" in marking
            // TODO Fix checking of unit type
            if (unit instanceof FollowerUnit) {
                UnitHandler.addControlledUnit(new ControlledUnit<>((FollowerUnit) unit));
            }
        }
    }

    private static void addControlledUnit(ControlledUnit controlledUnit) {
        logger.info("Controlled unit added -- Marking: " + controlledUnit.unit.getMarking());
        controlledUnits.add(controlledUnit);
    }

    public static Collection<Unit> getUnits() {
        return markingUnitMap.values();
    }

    public static void updateUnits(double timestamp) {
        for (Unit unit : markingUnitMap.values()) {
            unit.updateData(timestamp);
        }
    }

    // TODO Move method to experiment-dependent package
    private static boolean isFollower(String marking) {
        return marking.startsWith("F");
    }

    // TODO Move method to experiment-dependent package
    private static boolean isTarget(String marking) {
        return marking.startsWith("T");
    }

    private static String getUnitName(String marking) {
        if (isFollower(marking)) {
            return marking.substring(0, marking.indexOf('-'));
        }
        return marking;
    }

    // TODO Move method to experiment-dependent package
    private static String getTargetNameFromFollowerMarking(String marking) {
        return marking.substring(marking.indexOf('-') + 1, marking.length());
    }

    public static int getNumOfUnits() {
        return markingUnitMap.size();
    }

    public static void tickAllControlledUnits() {
        for (ControlledUnit controlledUnit : controlledUnits) {
            controlledUnit.sendUnitCommands();
        }
    }

    public static void reset() {
        targetFollowerMap = new HashMap<>();
        markingUnitMap = new HashMap<>();
        controlledUnits = new ArrayList<>();
    }
}
