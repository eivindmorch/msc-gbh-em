package core.model.btree.genops.mutations;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.model.btree.task.VariableLeafTask;
import core.unit.Unit;
import core.util.exceptions.NoSuchTasksFoundException;

public class RandomiseTaskVariablesMutation extends Mutation {

    public RandomiseTaskVariablesMutation(double weight) {
        super(weight);
    }

    @Override
    public boolean canBePerformed(Task root) {
        return !BehaviorTreeUtil.getTasks(root, true, VariableLeafTask.class).isEmpty();
    }

    @Override
    public Task mutate(Task root, Class<? extends Unit> unitClass) {
        try {
            Task newTreeRoot = BehaviorTreeUtil.clone(root);
            VariableLeafTask randomTask  = BehaviorTreeUtil.getRandomTask(newTreeRoot, true, VariableLeafTask.class);
            randomTask.randomiseVariables();
            return newTreeRoot;
        } catch (NoSuchTasksFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
