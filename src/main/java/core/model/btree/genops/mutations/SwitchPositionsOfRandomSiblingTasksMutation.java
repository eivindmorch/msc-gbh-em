package core.model.btree.genops.mutations;

import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.TempTask;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

import static core.util.SystemUtil.random;

public class SwitchPositionsOfRandomSiblingTasksMutation extends Mutation {

    public SwitchPositionsOfRandomSiblingTasksMutation(double weight) {
        super(weight);
    }

    @Override
    public boolean canBePerformed(TempTask root) {
        try {
            BehaviorTreeUtil.getRandomTask(root, true, TempCompositeTask.class, 2);
        } catch (NoSuchTaskFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public void mutate(TempTask root, Class<? extends Unit> unitClass) {
        try {
            TempCompositeTask randomRoot = BehaviorTreeUtil.getRandomTask(root, true, TempCompositeTask.class, 2);

            int childIndex1 = random.nextInt(randomRoot.getChildCount());
            int childIndex2;
            do {
                childIndex2 = random.nextInt(randomRoot.getChildCount());
            } while (childIndex1 == childIndex2);

            randomRoot.swapChildrenPositions(childIndex1, childIndex2);

        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
