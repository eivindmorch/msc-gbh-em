package experiments.experiment1.unit;

import core.unit.ControlledUnit;
import core.unit.Unit;
import core.unit.UnitHandler;
import core.unit.UnitTypeInfo;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Experiment1AddUnitMethod implements UnitHandler.AddUnitMethod{

    private static final Logger logger = LoggerFactory.getLogger(UnitHandler.class);

    private static HashMap<String, List<PhysicalEntityObject>> targetFollowerMap = new HashMap<>();

    @Override
    public void addUnit(PhysicalEntityObject physicalEntity) {
        String unitMarking = physicalEntity.getMarking().getMarking();
        String unitIdentifier = UnitHandler.getUnitIdentifier(unitMarking);
        ObjectInstanceHandle unitHandle = physicalEntity.getObjectInstanceHandle();

        Unit unit;
        if (isWanderer(unitMarking)) {
            unit = new Experiment1Unit(unitMarking, unitIdentifier, unitHandle);
        } else if (isFollower(unitMarking)) {
            String targetIdentifier = getTargetIdentifierFromFollowerMarking(unitMarking);
            Experiment1Unit targetUnit = (Experiment1Unit)UnitHandler.unitIdentifierToUnitMap.get(targetIdentifier);
            if (targetUnit != null) {
                unit = new FollowerUnit(unitMarking, unitIdentifier, unitHandle, targetUnit);
            } else {
                // Schedules Follower for initialisation when Target is initialised
                targetFollowerMap.computeIfAbsent(targetIdentifier, k -> new ArrayList<>());
                targetFollowerMap.get(targetIdentifier).add(physicalEntity);
                logger.info("FollowerUnit " + unitMarking + " was scheduled for later initialisation.");
                return;
            }
        } else {
            logger.warn("Unit " + unitMarking + " was NOT added due to no related role.");
            return;
        }

        // Initiates all scheduled Followers with this unit as Target
        if (UnitHandler.putUnit(unit)) {
            if (UnitHandler.shouldBeControlledUnit(unit)) {
                if (unit instanceof FollowerUnit) {
                    UnitHandler.addControlledUnit(new ControlledUnit<>((FollowerUnit)unit));
                }
            }
            addAllScheduledDependingUnits(unit);
        }
    }

    private void addAllScheduledDependingUnits(Unit unit) {
        if (targetFollowerMap.get(unit.getIdentifier()) != null) {
            targetFollowerMap.get(unit.getIdentifier()).forEach(UnitHandler::addUnit);
        }
    }

    private boolean isFollower(String marking) {
        String symbol = marking.substring(0, 1);
        return UnitTypeInfo.getUnitInfoFromSymbol(symbol).getUnitClass().equals(FollowerUnit.class);
    }

    private boolean isWanderer(String marking) {
        String symbol = marking.substring(0, 1);
        return UnitTypeInfo.getUnitInfoFromSymbol(symbol).getUnitClass().equals(Experiment1Unit.class);
    }

    private String getTargetIdentifierFromFollowerMarking(String marking) {
        String strippedMarking = UnitHandler.stripMarkingOfOptions(UnitHandler.stripMarkingOfComments(marking));
        int separatorIndex = strippedMarking.indexOf(UnitHandler.GOAL_SEPARATOR) + 1;
        return strippedMarking.substring(separatorIndex);
    }


    @Override
    public void reset() {
        targetFollowerMap = new HashMap<>();
    }
}
