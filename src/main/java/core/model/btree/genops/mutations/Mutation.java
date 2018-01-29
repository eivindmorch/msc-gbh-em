package core.model.btree.genops.mutations;

import com.badlogic.gdx.ai.btree.Task;
import core.unit.Unit;

public abstract class Mutation {

    private double weight;

    public Mutation(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public abstract boolean canBePerformed(Task root);

    public abstract Task mutate(Task root, Class<? extends Unit> unitClass);
}
