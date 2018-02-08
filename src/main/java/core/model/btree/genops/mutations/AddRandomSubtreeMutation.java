package core.model.btree.genops.mutations;

import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import com.sun.javaws.exceptions.InvalidArgumentException;
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
    public boolean canBePerformed(Task root) {
        return BehaviorTreeUtil.getSize(root) > 0;
    }

    @Override
    public Task mutate(Task root, Class<? extends Unit> unitClass) {
        try {

            Task randomSubtree;
            if (onlyAddSingleLeafTask) {
                List<Class<? extends Task>> availableLeafTasks = UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableLeafTasks();
                randomSubtree = availableLeafTasks.get(random.nextInt(availableLeafTasks.size())).newInstance();
            } else {
                randomSubtree = BehaviorTreeUtil.generateRandomTree(unitClass, 3, 5);
            }

            Task randomTaskInTree = BehaviorTreeUtil.getRandomTask(root, true, Task.class);

            if (randomTaskInTree instanceof BranchTask) {
                return BehaviorTreeUtil.insertTask(root, randomTaskInTree, random.nextInt(randomTaskInTree.getChildCount() + 1), randomSubtree);

            } else {
                List<Class<? extends BranchTask>> availableCompositeTasks = UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableCompositeTasks();
                BranchTask randomComposite = availableCompositeTasks.get(random.nextInt(availableCompositeTasks.size())).newInstance();

                if (random.nextBoolean()) {
                    randomComposite.addChild(randomTaskInTree);
                    randomComposite.addChild(randomSubtree);
                } else {
                    randomComposite.addChild(randomSubtree);
                    randomComposite.addChild(randomTaskInTree);
                }
                return BehaviorTreeUtil.replaceTask(root, randomTaskInTree, randomComposite);
            }

        } catch (InvalidArgumentException | IllegalAccessException | InstantiationException | NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
