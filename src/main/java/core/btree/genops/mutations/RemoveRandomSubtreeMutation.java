package core.btree.genops.mutations;

import core.BtreeAlt.TempTask;
import core.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

public class RemoveRandomSubtreeMutation extends Mutation {

    public RemoveRandomSubtreeMutation(double weight, double factor) {
        super(weight, factor);
    }

    @Override
    protected boolean canBePerformed(TempTask root) {
        // TODO Do we need more thorough check?
        return root.getSize() > 3;
    }

    @Override
    protected TempTask mutate(TempTask root, Class<? extends Unit> unitClass) {
        TempTask newRoot = root.cloneTask();
        try {
            TempTask randomRoot = newRoot.getRandomTask(false, TempTask.class);
            randomRoot.removeFromParent();
            return newRoot;
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
