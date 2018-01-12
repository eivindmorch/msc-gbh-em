package core.model.btree;

import core.unit.Unit;

public class Blackboard<T extends Unit> {

    private T unit;

    public Blackboard(T unit) {
        this.unit = unit;
    }

    public T getUnit() {
        return unit;
    }

    @Override
    public Blackboard clone() {
        return new Blackboard<>(this.unit);
    }
}
