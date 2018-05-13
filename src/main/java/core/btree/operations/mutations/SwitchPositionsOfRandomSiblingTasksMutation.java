package core.btree.operations.mutations;

import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.TempTask;
import core.btree.operations.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

import static core.util.SystemUtil.random;

public class SwitchPositionsOfRandomSiblingTasksMutation extends Mutation {

    public SwitchPositionsOfRandomSiblingTasksMutation(double weight, double factor) {
        super(weight, factor);
    }

    @Override
    protected boolean canBePerformed(TempTask root) {
        try {
            root.getRandomTask(true, TempCompositeTask.class, 2);
        } catch (NoSuchTaskFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    protected TempTask mutate(TempTask root, Class<? extends Unit> unitClass) {
        TempTask newRoot = root.cloneTask();
        try {
            TempCompositeTask randomRoot = newRoot.getRandomTask(true, TempCompositeTask.class, 2);

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
