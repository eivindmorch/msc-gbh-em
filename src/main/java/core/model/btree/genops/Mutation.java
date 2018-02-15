package core.model.btree.genops;

import core.BtreeAlt.TempTask;
import core.unit.Unit;

public abstract class Mutation {

    private double weight;

    public Mutation(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public abstract boolean canBePerformed(TempTask root);

    public abstract void mutate(TempTask root, Class<? extends Unit> unitClass);
}
