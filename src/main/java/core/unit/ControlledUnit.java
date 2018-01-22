package core.unit;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.Blackboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ControlledUnit<U extends Unit> {

    private final Logger logger = LoggerFactory.getLogger(ControlledUnit.class);

    U unit;
    private BehaviorTree<Blackboard<U>> btree;

    public ControlledUnit(U unit) {
        this.unit = unit;
        Blackboard<U> blackboard = new Blackboard<>(unit);
        this.btree = new BehaviorTree<>(ControlledUnit.controlledUnitBtreeMap.get(unit.getClass()), blackboard);
        System.out.println(btree);
        this.btree.setObject(new Blackboard<>(unit));
    }

    void sendUnitCommands() {
        if (btree == null) {
            logger.error("BehaviorTree is null for unit: " + unit);
            return;
        }
        btree.step();
    }

    private static HashMap<Class<? extends Unit>, Task> controlledUnitBtreeMap = new HashMap<>();
    public static void setControlledUnitBtreeMap(Class<? extends Unit> unitClass, Task rootTask) {
        HashMap<Class<? extends Unit>, Task> controlledUnitBtreeMap = new HashMap<>();
        controlledUnitBtreeMap.put(unitClass, rootTask);
        ControlledUnit.controlledUnitBtreeMap = controlledUnitBtreeMap;
    }
}
