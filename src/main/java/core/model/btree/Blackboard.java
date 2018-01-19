package core.model.btree;

import core.unit.Unit;

public class Blackboard<U extends Unit> {

    private U unit;

    public Blackboard(U unit) {
        this.unit = unit;
    }

    public U getUnit() {
        return unit;
    }

    @Override
    public Blackboard<U> clone() {
        return new Blackboard<>(this.unit);
    }
}
