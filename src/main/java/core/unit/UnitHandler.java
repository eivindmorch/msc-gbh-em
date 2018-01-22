package core.unit;

import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class UnitHandler {

    private static final Logger logger = LoggerFactory.getLogger(UnitHandler.class);

    public static HashMap<String, Unit> unitIdentifierToUnitMap = new HashMap<>();
    private static ArrayList<ControlledUnit> controlledUnits = new ArrayList<>();

    private static AddUnitMethod addUnitMethod;

    // Unit marking options
    public static char GOAL_SEPARATOR = '-';

    public static char OPTIONS_START = '[';
    public static char OPTIONS_END = ']';

    public static char COMMENTS_START = '(';
    public static char COMMENTS_END = ')';

    public static char CONTROLLED = 'c';


    public static void setAddUnitMethod(AddUnitMethod addUnitMethod) {
        UnitHandler.addUnitMethod = addUnitMethod;
    }

    public static void addUnit(PhysicalEntityObject physicalEntity) {
        if (addUnitMethod == null) {
            logger.warn("No addUnitMethod has been registered");
        } else {
            addUnitMethod.addUnit(physicalEntity);
        }
    }

    public static boolean putUnit(Unit unit) {
        if (unitIdentifierToUnitMap.get(unit.getIdentifier()) == null) {
            unitIdentifierToUnitMap.put(unit.getIdentifier(), unit);
            logger.info("Unit added -- Marking: " + unit.getMarking() + ", Handle: " + unit.getHandle());
            UnitLogger.register(unit);
            return true;
        }
        return false;
    }

    public static void addControlledUnit(ControlledUnit controlledUnit) {
        logger.info("Controlled units added -- Marking: " + controlledUnit.unit.getMarking());
        controlledUnits.add(controlledUnit);
    }

    public static Collection<Unit> getUnits() {
        return unitIdentifierToUnitMap.values();
    }

    public static void updateUnits(double timestamp) {
        for (Unit unit : unitIdentifierToUnitMap.values()) {
            unit.updateData(timestamp);
        }
    }

    public static int getNumOfUnits() {
        return unitIdentifierToUnitMap.size();
    }

    public static void tickAllControlledUnits() {
        for (ControlledUnit controlledUnit : controlledUnits) {
            controlledUnit.sendUnitCommands();
        }
    }

    public static String getUnitIdentifier(String marking) {
        String strippedMarking = stripMarkingOfOptions(stripMarkingOfComments(marking));
        int indexOfDash = strippedMarking.indexOf(GOAL_SEPARATOR);
        if (indexOfDash >= 0) {
            return strippedMarking.substring(0, strippedMarking.indexOf(GOAL_SEPARATOR));
        }
        return strippedMarking;
    }

    public static boolean shouldBeControlledUnit(Unit unit) {
        List<String> args = getUnitOptionArguments(unit.getMarking());
        return (args.contains(Character.toString(CONTROLLED)));
    }

    public static List<String> getUnitOptionArguments(String unitMarking) {
        int startIndex = unitMarking.indexOf(OPTIONS_START);
        int endIndex = unitMarking.indexOf(OPTIONS_END);
        if (startIndex >= 0 && endIndex > startIndex) {
            return Arrays.asList(unitMarking.substring(startIndex + 1, endIndex).split(","));
        }
        return new ArrayList<>();
    }

    public static String stripMarkingOfComments(String marking) {
        return marking.replaceAll("\\" + COMMENTS_START + ".*" + "\\" + COMMENTS_END, "");
    }

    public static String stripMarkingOfOptions(String marking) {
        return marking.replaceAll("\\" + OPTIONS_START + ".*" + OPTIONS_END, "");
    }

    public static void reset() {
        logger.info("Resetting unit storage.");
        unitIdentifierToUnitMap = new HashMap<>();
        controlledUnits = new ArrayList<>();
        if (addUnitMethod == null) {
            logger.warn("No addUnitMethod has been registered");
        } else {
            addUnitMethod.reset();
        }
    }


    public interface AddUnitMethod {
        void addUnit(PhysicalEntityObject physicalEntity);
        void reset();
    }
}