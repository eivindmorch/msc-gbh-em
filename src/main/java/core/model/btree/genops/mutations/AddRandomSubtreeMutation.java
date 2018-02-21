package core.model.btree.genops.mutations;

import com.sun.javaws.exceptions.InvalidArgumentException;
import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.LeafTasks.TempLeafTask;
import core.BtreeAlt.TempTask;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.unit.UnitTypeInfo;
import core.util.exceptions.NoSuchTaskFoundException;

import java.util.List;
import static core.util.SystemUtil.random;

public class AddRandomSubtreeMutation extends Mutation {

    private boolean onlyAddSingleLeafTask;

    public AddRandomSubtreeMutation(double weight, double factor, boolean onlyAddSingleLeafTask) {
        super(weight, factor);
        this.onlyAddSingleLeafTask = onlyAddSingleLeafTask;
    }

    @Override
    protected boolean canBePerformed(TempTask root) {
        return root.getSize() > 0;
    }

    @Override
    protected TempTask mutate(TempTask root, Class<? extends Unit> unitClass) {
        TempTask newRoot = root.cloneTask();

        try {
            TempTask randomSubtree;
            if (onlyAddSingleLeafTask) {
                randomSubtree = UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getRandomAvailableLeafTask();
            } else {
                randomSubtree = BehaviorTreeUtil.generateRandomTree(unitClass, 3, 5);
            }

            TempTask randomTaskInTree = newRoot.getRandomTask(true, TempTask.class);

            if (randomTaskInTree instanceof TempCompositeTask) {
                ((TempCompositeTask) randomTaskInTree).insertChild(random.nextInt(randomTaskInTree.getChildCount() + 1), randomSubtree);

            } else {
                TempCompositeTask randomComposite = UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getRandomAvailableCompositeTask();

                if (random.nextBoolean()) {
                    randomComposite.addChild(randomTaskInTree.cloneTask());
                    randomComposite.addChild(randomSubtree);
                } else {
                    randomComposite.addChild(randomSubtree);
                    randomComposite.addChild(randomTaskInTree.cloneTask());
                }
                randomTaskInTree.getParent().replaceChild(randomTaskInTree, randomComposite);
            }
            return newRoot;
        } catch (InvalidArgumentException | NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
