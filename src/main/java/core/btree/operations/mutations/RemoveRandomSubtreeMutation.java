package core.btree.operations.mutations;

import core.btree.tasks.blueprint.template.Task;
import core.btree.operations.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

public class RemoveRandomSubtreeMutation extends Mutation {

    public RemoveRandomSubtreeMutation(double weight, double factorBase) {
        super(weight, factorBase);
    }

    @Override
    protected boolean canBePerformed(Task root) {
        // TODO Do we need more thorough check?
        return root.getSize() > 3;
    }

    @Override
    protected Task mutate(Task root, Class<? extends Unit> unitClass) {
        Task newRoot = root.cloneTask();
        try {
            Task randomRoot = newRoot.getRandomTask(false, Task.class);
            randomRoot.removeFromParent();
            return newRoot;
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
