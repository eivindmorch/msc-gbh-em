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

    public AddRandomSubtreeMutation(double weight, boolean onlyAddSingleLeafTask) {
        super(weight);
        this.onlyAddSingleLeafTask = onlyAddSingleLeafTask;
    }

    @Override
    public boolean canBePerformed(TempTask root) {
        return BehaviorTreeUtil.getSize(root) > 0;
    }

    @Override
    public void mutate(TempTask root, Class<? extends Unit> unitClass) {
        try {

            TempTask randomSubtree;
            if (onlyAddSingleLeafTask) {
                List<Class<? extends TempLeafTask>> availableLeafTasks = UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableLeafTasks();
                randomSubtree = availableLeafTasks.get(random.nextInt(availableLeafTasks.size())).newInstance();
            } else {
                randomSubtree = BehaviorTreeUtil.generateRandomTree(unitClass, 3, 5);
            }

            TempTask randomTaskInTree = BehaviorTreeUtil.getRandomTask(root, true, TempTask.class);

            if (randomTaskInTree instanceof TempCompositeTask) {
                ((TempCompositeTask) randomTaskInTree).insertChild(random.nextInt(randomTaskInTree.getChildCount() + 1), randomSubtree);

            } else {
                List<Class<? extends TempCompositeTask>> availableCompositeTasks = UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableCompositeTasks();
                TempCompositeTask randomComposite = availableCompositeTasks.get(random.nextInt(availableCompositeTasks.size())).newInstance();

                if (random.nextBoolean()) {
                    randomComposite.addChild(randomTaskInTree.cloneTask());
                    randomComposite.addChild(randomSubtree);
                } else {
                    randomComposite.addChild(randomSubtree);
                    randomComposite.addChild(randomTaskInTree.cloneTask());
                }
                randomTaskInTree.getParent().replaceChild(randomTaskInTree, randomComposite);
            }
        } catch (InvalidArgumentException | IllegalAccessException | InstantiationException | NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
