package core.unit;

import core.model.btree.Blackboard;
import core.model.btree.GenBehaviorTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ControlledUnit<U extends Unit> {

    private final Logger logger = LoggerFactory.getLogger(ControlledUnit.class);

    U unit;
    private GenBehaviorTree<U> btree;

    public ControlledUnit(U unit) {
        this.unit = unit;
        this.btree = ControlledUnit.controlledUnitBtreeMap.get(unit.getClass()).clone();
        this.btree.setObject(new Blackboard<>(unit));
    }

    void sendUnitCommands() {
        if (btree == null) {
            logger.error("BehaviorTree is null for unit: " + unit);
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
