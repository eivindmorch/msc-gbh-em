package core.btree.operations.mutations;

import com.sun.javaws.exceptions.InvalidArgumentException;
import core.btree.tasks.modular.template.Task;
import core.btree.tasks.modular.template.composite.CompositeTask;
import core.btree.BehaviorTreeUtil;
import core.btree.operations.Mutation;
import core.unit.Unit;
import core.unit.UnitHandler;
import core.util.exceptions.NoSuchTaskFoundException;

import static core.util.SystemUtil.random;

public class AddRandomSubtreeMutation extends Mutation {

    private boolean onlyAddSingleLeafTask;

    public AddRandomSubtreeMutation(double weight, double factor, boolean onlyAddSingleLeafTask) {
        super(weight, factor);
        this.onlyAddSingleLeafTask = onlyAddSingleLeafTask;
    }

    @Override
    protected boolean canBePerformed(Task root) {
        return root.getSize() > 0;
    }

    @Override
    protected Task mutate(Task root, Class<? extends Unit> unitClass) {
        Task newRoot = root.cloneTask();

        try {
            Task randomSubtree;
            if (onlyAddSingleLeafTask) {
                randomSubtree = UnitHandler.getUnitTypeInfoFromUnitClass(unitClass).getRandomAvailableLeafTask();
            } else {
                randomSubtree = BehaviorTreeUtil.generateRandomTree(unitClass, 3, 5);
            }

            Task randomTaskInTree = newRoot.getRandomTask(true, Task.class);

            if (randomTaskInTree instanceof CompositeTask) {
                ((CompositeTask) randomTaskInTree).insertChild(random.nextInt(randomTaskInTree.getChildCount() + 1), randomSubtree);

            } else {
                CompositeTask randomComposite = UnitHandler.getUnitTypeInfoFromUnitClass(unitClass).getRandomAvailableCompositeTask();

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
