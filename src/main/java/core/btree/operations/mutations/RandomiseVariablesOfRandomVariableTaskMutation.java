package core.btree.operations.mutations;

import core.btree.tasks.modular.template.Task;
import core.btree.tasks.modular.template.leaf.VariableLeafTask;
import core.btree.operations.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

public class RandomiseVariablesOfRandomVariableTaskMutation extends Mutation {

    public RandomiseVariablesOfRandomVariableTaskMutation(double weight, double factorBase) {
        super(weight, factorBase);
    }

    @Override
    protected boolean canBePerformed(Task root) {
        try {
            root.getRandomTask(true, VariableLeafTask.class);
            return true;
        } catch (NoSuchTaskFoundException e) {
            return false;
        }
    }

    @Override
    protected Task mutate(Task root, Class<? extends Unit> unitClass) {
        Task newRoot = root.cloneTask();
        try {
            VariableLeafTask randomTask  = newRoot.getRandomTask(true, VariableLeafTask.class);
            randomTask.randomiseVariables();
            return newRoot;
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
