package unit;

import model.btree.Blackboard;
import model.btree.GenBehaviorTree;
import model.btree.task.Move;

public class ControlledUnit {

    Unit unit;
    GenBehaviorTree btree;

    public ControlledUnit(Unit unit) {
        this.unit = unit;
        Blackboard blackboard = new Blackboard((FollowerUnit)unit);
        this.btree = new GenBehaviorTree(new Move(), blackboard);
    }

    public void tick() {
        btree.step();
    }
}
