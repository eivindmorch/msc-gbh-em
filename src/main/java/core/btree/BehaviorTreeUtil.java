package core.btree;

import com.sun.javaws.exceptions.InvalidArgumentException;
import core.btree.tasks.modular.template.Task;
import core.btree.tasks.modular.template.composite.CompositeTask;
import core.btree.tasks.modular.template.composite.Selector;
import core.btree.tasks.modular.template.composite.Sequence;
import core.btree.tasks.modular.template.leaf.LeafTask;
import core.unit.Unit;
import core.unit.UnitTypeInfo;
import experiments.experiment1.tasks.modular.IsApproachingTask;
import experiments.experiment1.tasks.modular.IsWithinTask;
import experiments.experiment1.tasks.modular.MoveToTargetTask;
import experiments.experiment1.tasks.modular.TurnToTargetTask;

import java.util.*;

import static core.util.SystemUtil.random;

@SuppressWarnings("WeakerAccess")
public abstract class BehaviorTreeUtil {

    // TODO Test
    @SuppressWarnings("UnnecessaryLocalVariable")
    public static Task generateRandomTree(Class<? extends Unit> unitClass, int minimumTasks, int maximumTasks) throws InvalidArgumentException {

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

        Task root;
        do {
            root = generateRandomTreeWithSpecificNumberOfTasks(unitClass, numberOfCompositeTasks, numberOfLeafTasks);
            root = root.getCleanVersion();
        } while (root.getSize() < minimumTasks || root.getSize() > maximumTasks);
        return root;
    }

    private static Task generateRandomTreeWithSpecificNumberOfTasks(Class<? extends Unit> unitClass, int numberOfCompositeTasks, int numberOfLeafTasks)
            throws InvalidArgumentException {

        if (numberOfLeafTasks < numberOfCompositeTasks + 1) {
            throw new InvalidArgumentException(new String[]{"Must have (numberOfCompositeTasks + 1) leaf tasks"});
        }

        UnitTypeInfo unitTypeInfo = UnitTypeInfo.getUnitInfoFromUnitClass(unitClass);

        // Create pool of random composite tasks
        ArrayList<CompositeTask> unassignedCompositeTasks = new ArrayList<>(numberOfCompositeTasks);
        for (int i = 0; i < numberOfCompositeTasks; i++) {
            unassignedCompositeTasks.add(unitTypeInfo.getRandomAvailableCompositeTask());
        }

        // Create pool of random leaf tasks
        ArrayList<LeafTask> unassignedLeafTasks = new ArrayList<>(numberOfLeafTasks);
        for (int i = 0; i < numberOfLeafTasks; i++) {
            unassignedLeafTasks.add(unitTypeInfo.getRandomAvailableLeafTask());
        }

        // Contains list of all composites added to the tree
        ArrayList<CompositeTask> compositeTasksAddedToTree = new ArrayList<>();

        // Add root
        compositeTasksAddedToTree.add(unassignedCompositeTasks.remove(0));

        // Children needed to be added to specific composites to satisfy requirement of at least 2 children per composite
        int necessaryChildren = 2;

        while (unassignedCompositeTasks.size() + necessaryChildren > numberOfLeafTasks
                && !unassignedCompositeTasks.isEmpty()) {
            CompositeTask compositeTaskToBeAdded = unassignedCompositeTasks.remove(0);
            CompositeTask chosenParent = compositeTasksAddedToTree.get(random.nextInt(compositeTasksAddedToTree.size()));

            if (chosenParent.getChildCount() < 2) {
                necessaryChildren -= 1;
            }
            necessaryChildren += 2;
            chosenParent.addChild(compositeTaskToBeAdded);
            compositeTasksAddedToTree.add(compositeTaskToBeAdded);
        }

        while (!unassignedCompositeTasks.isEmpty()) {
            CompositeTask compositeTaskToBeAdded = unassignedCompositeTasks.remove(0);

            ArrayList<CompositeTask> addedCompositeTasksWithLessThan2Children = new ArrayList<>();
            for (CompositeTask compositeTaskAddedToTree : compositeTasksAddedToTree) {
                if (compositeTaskAddedToTree.getChildCount() < 2) {
                    addedCompositeTasksWithLessThan2Children.add(compositeTaskAddedToTree);
                }
            }
            CompositeTask chosenParent = addedCompositeTasksWithLessThan2Children.get(
                    random.nextInt(addedCompositeTasksWithLessThan2Children.size()
                    )
            );
            chosenParent.addChild(compositeTaskToBeAdded);
            compositeTasksAddedToTree.add(compositeTaskToBeAdded);
        }

        // Make sure all composite tasks have at least 2 children
        for (CompositeTask compositeTaskAddedToTree : compositeTasksAddedToTree) {
            while (compositeTaskAddedToTree.getChildCount() < 2) {
                compositeTaskAddedToTree.addChild(unassignedLeafTasks.remove(0));
            }
        }

        // Add remaining leaf tasks to random composite task child list
        for (LeafTask leafTask : unassignedLeafTasks) {
            compositeTasksAddedToTree.get(random.nextInt(compositeTasksAddedToTree.size())).addChild(leafTask);
        }

        // Shuffle children lists
        for (CompositeTask compositeTaskAddedToTree : compositeTasksAddedToTree) {
            compositeTaskAddedToTree.shuffleChildren();
        }

        // Return root
        return compositeTasksAddedToTree.get(0);
    }


    public static Task generateTestTree() {
        Selector shouldMoveSelector = new Selector(new IsApproachingTask(20), new IsWithinTask(30));
        Sequence shouldNotMoveSequence = new Sequence(shouldMoveSelector, new TurnToTargetTask());
        return new Selector(shouldNotMoveSequence, new MoveToTargetTask());
    }

}
