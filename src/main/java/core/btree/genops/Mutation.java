package core.btree.genops;

import core.BtreeAlt.TempTask;
import core.unit.Unit;

public abstract class Mutation {

    private double weight;
    private double factor;

    public Mutation(double weight, double factor) {
        this.weight = weight;
        this.factor = factor;
    }

    double getWeight(double factorPower) {
        return weight * Math.pow(factor, factorPower);
    }

    protected abstract boolean canBePerformed(TempTask root);

    protected abstract TempTask mutate(TempTask root, Class<? extends Unit> unitClass);
}
