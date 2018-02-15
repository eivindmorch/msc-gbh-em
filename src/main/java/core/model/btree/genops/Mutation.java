package core.model.btree.genops;

import core.BtreeAlt.TempTask;
import core.unit.Unit;

public abstract class Mutation {

    private double weight;

    public Mutation(double weight) {
        this.weight = weight;
    }

    double getWeight() {
        return weight;
    }

    protected abstract boolean canBePerformed(TempTask root);

    protected abstract TempTask mutate(TempTask root, Class<? extends Unit> unitClass);
}
