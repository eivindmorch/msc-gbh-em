package core.btree.operations.mutations;

import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.TempTask;
import core.btree.operations.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

public class ReplaceTreeWithSubtreeMutation extends Mutation{

    public ReplaceTreeWithSubtreeMutation(double weight, double factor) {
        super(weight, factor);
    }

    @Override
    protected boolean canBePerformed(TempTask root) {
        try {
            root.getRandomTask(false, TempCompositeTask.class, 2);
            return true;
        } catch (NoSuchTaskFoundException e) {
            return false;
        }
    }

    @Override
    protected TempTask mutate(TempTask root, Class<? extends Unit> unitClass) {
        TempTask newRoot = root.cloneTask();
        try {
            return newRoot.getRandomTask(false, TempCompositeTask.class, 2);
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
