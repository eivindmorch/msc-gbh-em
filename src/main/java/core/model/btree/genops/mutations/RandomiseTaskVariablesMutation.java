package core.model.btree.genops.mutations;

import com.badlogic.gdx.ai.btree.Task;
import core.unit.Unit;

public class RandomiseTaskVariablesMutation extends Mutation {

    public RandomiseTaskVariablesMutation(double weight) {
        super(weight);
    }

    @Override
    public boolean canBePerformed(Task root) {
        return false;
    }

    @Override
    public Task mutate(Task root, Class<? extends Unit> unitClass) {
        return null;
    }
}
