package unit;

import model.btree.Blackboard;
import model.btree.GenBehaviorTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SystemStatus;

import java.util.HashMap;

public class ControlledUnit<T extends Unit> {

    private final Logger logger = LoggerFactory.getLogger(ControlledUnit.class);

    T unit;
    private GenBehaviorTree<T> btree;

    ControlledUnit(T unit) {
        this.unit = unit;
        this.btree = ControlledUnit.controlledUnitBtreeMap.get(unit.getClass()).clone();
        this.btree.setObject(new Blackboard<>(unit));
    }

    void sendUnitCommands() {
        if (btree == null) {
            logger.error("BehaviorTree is null!");
            return;
        }
        btree.step();
    }

    private static HashMap<Class<? extends Unit>, GenBehaviorTree> controlledUnitBtreeMap = new HashMap<>();
    public static void setControlledUnitBtreeMap(Class<? extends Unit> unitClass, GenBehaviorTree btree) {
        HashMap<Class<? extends Unit>, GenBehaviorTree> controlledUnitBtreeMap = new HashMap<>();
        controlledUnitBtreeMap.put(unitClass, btree);
        ControlledUnit.controlledUnitBtreeMap = controlledUnitBtreeMap;
    }
}
