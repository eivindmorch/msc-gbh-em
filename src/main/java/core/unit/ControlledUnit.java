package core.unit;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.TempTask;
import core.btree.Blackboard;
import core.util.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ControlledUnit<U extends Unit> {

    private final Logger logger = LoggerFactory.getLogger(ControlledUnit.class);

    private U unit;
    private BehaviorTree<Blackboard<U>> btree;

    public ControlledUnit(U unit) {
        this.unit = unit;
        Blackboard<U> blackboard = new Blackboard<>(unit);
        this.btree = new BehaviorTree<>(ControlledUnit.getControlledUnitBtree(unit), blackboard);
    }

    public U getUnit() {
        return this.unit;
    }

    void sendUnitCommands() {
        if (btree == null) {
            logger.error("BehaviorTree is null for unit: " + unit);
            return;
        }
        btree.step();
    }

    private static HashMap<Class<? extends Unit>, TempTask> controlledUnitBtreeMap = new HashMap<>();

    public static void setControlledUnitBtreeMap(Class<? extends Unit> unitClass, TempTask rootTask) {
        HashMap<Class<? extends Unit>, TempTask> controlledUnitBtreeMap = new HashMap<>();
        controlledUnitBtreeMap.put(unitClass, rootTask);
        ControlledUnit.controlledUnitBtreeMap = controlledUnitBtreeMap;
    }

    @SuppressWarnings("unchecked")
    private static <U extends Unit> Task<Blackboard<U>> getControlledUnitBtree(U unit) {
        return ControlledUnit.controlledUnitBtreeMap.get(unit.getClass()).instantiateTask();
    }

    @Override
    public String toString() {
        return ToStringBuilder.toStringBuilder(this)
                .add("unit", unit)
                .add("btree", btree)
                .toString();
    }
}
