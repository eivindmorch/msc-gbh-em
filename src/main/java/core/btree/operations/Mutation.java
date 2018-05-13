package core.btree.operations;

import core.btree.tasks.modular.template.Task;
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

    protected abstract boolean canBePerformed(Task root);

    protected abstract Task mutate(Task root, Class<? extends Unit> unitClass);
}
