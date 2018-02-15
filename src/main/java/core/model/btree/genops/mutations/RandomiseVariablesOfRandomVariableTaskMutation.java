package core.model.btree.genops.mutations;

import core.BtreeAlt.LeafTasks.TempVariableLeafTask;
import core.BtreeAlt.TempTask;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

public class RandomiseVariablesOfRandomVariableTaskMutation extends Mutation {

    public RandomiseVariablesOfRandomVariableTaskMutation(double weight) {
        super(weight);
    }

    @Override
    public boolean canBePerformed(TempTask root) {
        return !BehaviorTreeUtil.getTasks(root, true, TempVariableLeafTask.class).isEmpty();
    }

    @Override
    public void mutate(TempTask root, Class<? extends Unit> unitClass) {
        try {
            TempVariableLeafTask randomTask  = BehaviorTreeUtil.getRandomTask(root, true, TempVariableLeafTask.class);
            randomTask.randomiseVariables();
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
