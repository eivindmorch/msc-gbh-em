package unit;

import model.btree.Blackboard;
import model.btree.GenBehaviorTree;
import util.SystemStatus;

class ControlledUnit {

    Unit unit;
    GenBehaviorTree btree;

    // TODO Generalise
    ControlledUnit(FollowerUnit followerUnit) {
        this.unit = followerUnit;
        SystemStatus.controlledUnitBtreeMap.get(unit.getClass()).setObject(new Blackboard(followerUnit));
        this.btree = SystemStatus.controlledUnitBtreeMap.get(unit.getClass()).clone();
    }

    void sendUnitCommands() {
        if (btree == null) {
            System.out.println("null tree");
            return;
        }
        btree.step();
    }
}
