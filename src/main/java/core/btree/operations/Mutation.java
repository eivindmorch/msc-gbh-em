package core.btree.operations;

import core.btree.tasks.modular.template.Task;
import core.unit.Unit;

public abstract class Mutation {

    private double weight;
    private double factorBase;

    public Mutation(double weight, double factorBase) {
        this.weight = weight;
        this.factorBase = factorBase;
    }

    double getWeight(double factorExponent) {
        return weight * Math.pow(factorBase, factorExponent);
    }

    protected abstract boolean canBePerformed(Task root);

    protected abstract Task mutate(Task root, Class<? extends Unit> unitClass);
}
