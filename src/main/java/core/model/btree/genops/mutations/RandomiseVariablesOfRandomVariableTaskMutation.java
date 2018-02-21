package core.model.btree.genops.mutations;

import core.BtreeAlt.LeafTasks.TempVariableLeafTask;
import core.BtreeAlt.TempTask;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

public class RandomiseVariablesOfRandomVariableTaskMutation extends Mutation {

    public RandomiseVariablesOfRandomVariableTaskMutation(double weight, double factor) {
        super(weight, factor);
    }

    @Override
    protected boolean canBePerformed(TempTask root) {
        try {
            root.getRandomTask(true, TempVariableLeafTask.class);
            return true;
        } catch (NoSuchTaskFoundException e) {
            return false;
        }
    }

    @Override
    protected TempTask mutate(TempTask root, Class<? extends Unit> unitClass) {
        TempTask newRoot = root.cloneTask();
        try {
            TempVariableLeafTask randomTask  = newRoot.getRandomTask(true, TempVariableLeafTask.class);
            randomTask.randomiseVariables();
            return newRoot;
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
