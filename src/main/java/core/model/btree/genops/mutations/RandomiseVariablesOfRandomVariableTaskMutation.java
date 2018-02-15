package core.model.btree.genops.mutations;

import core.BtreeAlt.LeafTasks.TempVariableLeafTask;
import core.BtreeAlt.TempTask;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

public class RandomiseVariablesOfRandomVariableTaskMutation extends Mutation {

    public RandomiseVariablesOfRandomVariableTaskMutation(double weight) {
        super(weight);
    }

    @Override
    public boolean canBePerformed(TempTask root) {
        return !root.getTasks(true, TempVariableLeafTask.class).isEmpty();
    }

    @Override
    public void mutate(TempTask root, Class<? extends Unit> unitClass) {
        try {
            TempVariableLeafTask randomTask  = root.getRandomTask(true, TempVariableLeafTask.class);
            randomTask.randomiseVariables();
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
