package core.btree.operations.mutations;

import core.btree.tasks.blueprint.template.composite.CompositeTask;
import core.btree.tasks.blueprint.template.Task;
import core.btree.operations.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

import static core.util.SystemUtil.random;

public class SwitchPositionsOfRandomSiblingTasksMutation extends Mutation {

    public SwitchPositionsOfRandomSiblingTasksMutation(double weight, double factorBase) {
        super(weight, factorBase);
    }

    @Override
    protected boolean canBePerformed(Task root) {
        try {
            root.getRandomTask(true, CompositeTask.class, 2);
        } catch (NoSuchTaskFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    protected Task mutate(Task root, Class<? extends Unit> unitClass) {
        Task newRoot = root.cloneTask();
        try {
            CompositeTask randomRoot = newRoot.getRandomTask(true, CompositeTask.class, 2);

            int childIndex1 = random.nextInt(randomRoot.getChildCount());
            int childIndex2;
            do {
                childIndex2 = random.nextInt(randomRoot.getChildCount());
            } while (childIndex1 == childIndex2);

            randomRoot.swapChildrenPositions(childIndex1, childIndex2);

            return newRoot;

        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
