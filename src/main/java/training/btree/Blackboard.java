package training.btree;

import datalogging.Unit;

public class Blackboard {

    public Unit unit;

    public Blackboard(Unit unit) {
        this.unit = unit;
    }

    public Blackboard clone() {
        return new Blackboard(this.unit);
    }
}
