package core.btree.operations.mutations;

import core.btree.tasks.blueprint.template.Task;
import core.btree.tasks.blueprint.template.composite.CompositeTask;
import core.btree.operations.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

public class ReplaceTreeWithSubtreeMutation extends Mutation{

    public ReplaceTreeWithSubtreeMutation(double weight, double factorBase) {
        super(weight, factorBase);
    }

    @Override
    protected boolean canBePerformed(Task root) {
        try {
            root.getRandomTask(false, CompositeTask.class, 2);
            return true;
        } catch (NoSuchTaskFoundException e) {
            return false;
        }
    }

    @Override
    protected Task mutate(Task root, Class<? extends Unit> unitClass) {
        Task newRoot = root.cloneTask();
        try {
            return newRoot.getRandomTask(false, CompositeTask.class, 2);
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
