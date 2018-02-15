package core.model.btree;

import com.sun.javaws.exceptions.InvalidArgumentException;
import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.CompositeTasks.TempSelector;
import core.BtreeAlt.CompositeTasks.TempSequence;
import core.BtreeAlt.LeafTasks.TempLeafTask;
import core.BtreeAlt.TempTask;
import core.unit.Unit;
import core.unit.UnitTypeInfo;
import experiments.experiment1.tasks.temp.TempIsApproachingTask;
import experiments.experiment1.tasks.temp.TempIsWithinTask;
import experiments.experiment1.tasks.temp.TempMoveToTargetTask;
import experiments.experiment1.tasks.temp.TempTurnToTargetTask;

import java.util.*;

import static core.util.SystemUtil.random;

@SuppressWarnings("WeakerAccess")
public abstract class BehaviorTreeUtil {

    // TODO Test
    @SuppressWarnings("UnnecessaryLocalVariable")
    public static TempTask generateRandomTree(Class<? extends Unit> unitClass, int minimumTasks, int maximumTasks) throws InvalidArgumentException {

        if (minimumTasks < 3) {
            throw new InvalidArgumentException(new String[]{"minimumTasks must be 3 or more"});
        }
        if (minimumTasks > maximumTasks) {
            throw new InvalidArgumentException(new String[]{"minimumTasks cannot be larger than maximumTasks"});
        }

        int numberOfTasks = random.nextInt(maximumTasks - minimumTasks + 1) + minimumTasks;
        int halfOfNumberOfTasks = (int) Math.round(numberOfTasks / 2.0);
        int randomOfHalf = random.nextInt(halfOfNumberOfTasks - 1) + 1;

        int numberOfCompositeTasks = randomOfHalf;
        int numberOfLeafTasks = numberOfTasks - randomOfHalf;

        TempTask root;
        do {
            root = generateRandomTreeWithSpecificNumberOfTasks(unitClass, numberOfCompositeTasks, numberOfLeafTasks);
            root.clean();
        } while (root.getSize() < minimumTasks || root.getSize() > maximumTasks);
        return root;
    }

    private static TempTask generateRandomTreeWithSpecificNumberOfTasks(Class<? extends Unit> unitClass, int numberOfCompositeTasks, int numberOfLeafTasks)
            throws InvalidArgumentException {

        if (numberOfLeafTasks < numberOfCompositeTasks + 1) {
            throw new InvalidArgumentException(new String[]{"Must have (numberOfCompositeTasks + 1) leaf tasks"});
        }

        UnitTypeInfo unitTypeInfo = UnitTypeInfo.getUnitInfoFromUnitClass(unitClass);

        // Create pool of random composite tasks
        ArrayList<TempCompositeTask> unassignedCompositeTasks = new ArrayList<>(numberOfCompositeTasks);
        for (int i = 0; i < numberOfCompositeTasks; i++) {
            unassignedCompositeTasks.add(unitTypeInfo.getRandomAvailableCompositeTask());
        }

        // Create pool of random leaf tasks
        ArrayList<TempLeafTask> unassignedLeafTasks = new ArrayList<>(numberOfLeafTasks);
        for (int i = 0; i < numberOfLeafTasks; i++) {
            unassignedLeafTasks.add(unitTypeInfo.getRandomAvailableLeafTask());
        }

        // Contains list of all composites added to the tree
        ArrayList<TempCompositeTask> compositeTasksAddedToTree = new ArrayList<>();

        // Add root
        compositeTasksAddedToTree.add(unassignedCompositeTasks.remove(0));

        // Children needed to be added to specific composites to satisfy requirement of at least 2 children per composite
        int necessaryChildren = 2;

        while (unassignedCompositeTasks.size() + necessaryChildren > numberOfLeafTasks
                && !unassignedCompositeTasks.isEmpty()) {
            TempCompositeTask compositeTaskToBeAdded = unassignedCompositeTasks.remove(0);
            TempCompositeTask chosenParent = compositeTasksAddedToTree.get(random.nextInt(compositeTasksAddedToTree.size()));

            if (chosenParent.getChildCount() < 2) {
                necessaryChildren -= 1;
            }
            necessaryChildren += 2;
            chosenParent.addChild(compositeTaskToBeAdded);
            compositeTasksAddedToTree.add(compositeTaskToBeAdded);
        }

        while (!unassignedCompositeTasks.isEmpty()) {
            TempCompositeTask compositeTaskToBeAdded = unassignedCompositeTasks.remove(0);

            ArrayList<TempCompositeTask> addedCompositeTasksWithLessThan2Children = new ArrayList<>();
            for (TempCompositeTask compositeTaskAddedToTree : compositeTasksAddedToTree) {
                if (compositeTaskAddedToTree.getChildCount() < 2) {
                    addedCompositeTasksWithLessThan2Children.add(compositeTaskAddedToTree);
                }
            }
            TempCompositeTask chosenParent = addedCompositeTasksWithLessThan2Children.get(
                    random.nextInt(addedCompositeTasksWithLessThan2Children.size()
                    )
            );
            chosenParent.addChild(compositeTaskToBeAdded);
            compositeTasksAddedToTree.add(compositeTaskToBeAdded);
        }

        // Make sure all composite tasks have at least 2 children
        for (TempCompositeTask compositeTaskAddedToTree : compositeTasksAddedToTree) {
            while (compositeTaskAddedToTree.getChildCount() < 2) {
                compositeTaskAddedToTree.addChild(unassignedLeafTasks.remove(0));
            }
        }

        // Add remaining leaf tasks to random composite task child list
        for (TempLeafTask leafTask : unassignedLeafTasks) {
            compositeTasksAddedToTree.get(random.nextInt(compositeTasksAddedToTree.size())).addChild(leafTask);
        }

        // Shuffle children lists
        for (TempCompositeTask compositeTaskAddedToTree : compositeTasksAddedToTree) {
            compositeTaskAddedToTree.shuffleChildren();
        }

        // Return root
        return compositeTasksAddedToTree.get(0);
    }


    public static TempTask generateTestTree() {
        TempSelector shouldMoveSelector = new TempSelector(new TempIsApproachingTask(20), new TempIsWithinTask(30));
        TempSequence shouldNotMoveSequence = new TempSequence(shouldMoveSelector, new TempTurnToTargetTask());
        return new TempSelector(shouldNotMoveSequence, new TempMoveToTargetTask());
    }

}
