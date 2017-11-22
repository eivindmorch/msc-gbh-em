package training.btree;

import datalogging.Unit;

public class Blackboard {

    public Unit unit;

    public Unit otherUnit;

    public Blackboard(Unit unit, Unit otherUnit) {
        this.unit = unit;
        this.otherUnit = otherUnit;
    }

    public Blackboard clone() {
        return new Blackboard(this.unit, this.otherUnit);
    }
}
