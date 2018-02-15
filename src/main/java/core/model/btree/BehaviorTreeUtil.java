package core.model.btree;

import com.sun.javaws.exceptions.InvalidArgumentException;
import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.CompositeTasks.TempSelector;
import core.BtreeAlt.CompositeTasks.TempSequence;
import core.BtreeAlt.LeafTasks.TempLeafTask;
import core.BtreeAlt.TempAlwaysSuccessfulTask;
import core.BtreeAlt.TempTask;
import core.unit.Unit;
import core.unit.UnitTypeInfo;
import core.util.exceptions.NoSuchTaskFoundException;
import experiments.experiment1.tasks.temp.TempIsApproachingTask;
import experiments.experiment1.tasks.temp.TempIsWithinTask;
import experiments.experiment1.tasks.temp.TempMoveToTargetTask;
import experiments.experiment1.tasks.temp.TempTurnToTargetTask;

import java.util.*;

import static core.util.SystemUtil.random;

// TODO Doc all methods
@SuppressWarnings("WeakerAccess")
public abstract class BehaviorTreeUtil {

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

        try {
            return generateRandomTreeWithSpecificNumberOfTasks(unitClass, numberOfCompositeTasks, numberOfLeafTasks);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static TempTask generateRandomTreeWithSpecificNumberOfTasks(Class<? extends Unit> unitClass, int numberOfCompositeTasks, int numberOfLeafTasks)
            throws InvalidArgumentException {

        if (numberOfLeafTasks < numberOfCompositeTasks + 1) {
            throw new InvalidArgumentException(new String[]{"Must have (numberOfCompositeTasks + 1) leaf tasks"});
        }

        UnitTypeInfo unitTypeInfo = UnitTypeInfo.getUnitInfoFromUnitClass(unitClass);
        List<Class<? extends TempCompositeTask>> availableCompositeTasks = unitTypeInfo.getAvailableCompositeTasks();
        List<Class<? extends TempLeafTask>> availableLeafTasks = unitTypeInfo.getAvailableLeafTasks();

        // Create pool of random composite tasks
        ArrayList<TempCompositeTask> unassignedCompositeTasks = new ArrayList<>(numberOfCompositeTasks);
        for (int i = 0; i < numberOfCompositeTasks; i++) {

            Class<? extends TempCompositeTask> randomCompositeTaskClass = availableCompositeTasks.get(random.nextInt(availableCompositeTasks.size()));
            try {
                unassignedCompositeTasks.add(randomCompositeTaskClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Create pool of random leaf tasks
        ArrayList<TempLeafTask> unassignedLeafTasks = new ArrayList<>(numberOfLeafTasks);
        for (int i = 0; i < numberOfLeafTasks; i++) {

            Class<? extends TempLeafTask> randomLeafTaskClass = availableLeafTasks.get(random.nextInt(availableLeafTasks.size()));
            try {
                unassignedLeafTasks.add(randomLeafTaskClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
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


    public static int getSize(TempTask root) {
        int size = 1;

        for (TempTask child : root.getChildren()) {
            size += getSize(child);
        }
        return size;
    }

    public static int getDepth(TempTask root) {
        ArrayList<Integer> childDepths = new ArrayList<>();

        for (TempTask child : root.getChildren()) {
            childDepths.add(getDepth(child));
        }
        if (!childDepths.isEmpty()) {
            return 1 + Collections.max(childDepths);
        }
        return 1;
    }

    /**
     * Gets all tasks of the specified type.
     * @param root the root {@link TempTask} of the behavior tree that is to be searched
     * @param includeRoot whether to include the root in the selection process
     * @param taskTypeToSelect the type of task to be selected
     * @param <T> the type of {@code taskTypeToSelect}
     * @return a {@link List} of all tasks of type {@code taskTypeToSelect}
     */
    public static <T extends TempTask> ArrayList<T> getTasks(TempTask root, boolean includeRoot, Class<T> taskTypeToSelect) {
        ArrayList<T> tasks = new ArrayList<>();

        if (includeRoot && taskTypeToSelect.isInstance(root)) {
            tasks.add((T) root);
        }

        Stack<TempTask> stack = new Stack<>();
        stack.add(root);

        while (!stack.empty()) {
            TempTask currentRoot = stack.pop();

            stack.addAll(currentRoot.getChildren());

            for (TempTask child : currentRoot.getChildren()) {
                if (taskTypeToSelect.isInstance(child)) {
                    tasks.add((T) child);
                }
            }
        }
        return tasks;
    }

    /**
     * Selects a random {@link TempTask} of the specified type from an already existing behavior tree.
     * @param root the root {@link TempTask} of the behavior tree that is to be searched
     * @param includeRoot whether to include the root in the selection process
     * @param taskTypeToSelect the type of task to be selected
     * @return the randomly selected {@link TempTask}
     * @param <T> the type of {@code taskTypeToSelect}
     * @throws NoSuchTaskFoundException tree does not contain any tasks meeting the specified requirements
     */
    public static <T extends TempTask> T getRandomTask(TempTask root, boolean includeRoot, Class<T> taskTypeToSelect) throws NoSuchTaskFoundException {
        ArrayList<T> tasks = getTasks(root, includeRoot, taskTypeToSelect);

        if (tasks.size() == 0) {
            throw new NoSuchTaskFoundException();
        }
        return tasks.get(random.nextInt(tasks.size()));
    }

    /**
     * Selects a random {@link TempTask} of the specified type and with the specified minimum number of children
     * from an already existing behavior tree.
     * existing behavior tree, excluding the root {@link TempTask}.
     * @param root the root {@link TempTask} of the behavior tree that is to be searched
     * @param includeRoot whether to include the root in the selection process
     * @param taskTypeToSelect the type of task to be selected
     * @param minimumNumberOfChildren the minimum number of children the selected composite task ({@link TempCompositeTask}) can have
     * @param <T> the type of {@code taskTypeToSelect}
     * @return the randomly selected composite task ({@link TempCompositeTask}). {@code Null} if none was found.
     * @throws NoSuchTaskFoundException tree does not contain any tasks meeting the specified requirements
     */
    public static <T extends TempTask> T getRandomTask(TempTask root, boolean includeRoot, Class<T> taskTypeToSelect, int minimumNumberOfChildren) throws NoSuchTaskFoundException {
        ArrayList<T> tasks = getTasks(root, includeRoot, taskTypeToSelect);
        ArrayList<T> selectionTasks = new ArrayList<>();

        for (T task : tasks) {
            if (task.getChildCount() >= minimumNumberOfChildren) {
                selectionTasks.add(task);
            }
        }
        if (selectionTasks.size() == 0) {
            throw new NoSuchTaskFoundException();
        }
        return selectionTasks.get(random.nextInt(selectionTasks.size()));
    }

    public static void removeEmptyAndSingleChildCompositeTasks(TempTask root) {
        ArrayList<TempCompositeTask> compositeTasks = getTasks(root, false, TempCompositeTask.class);

        for (TempCompositeTask compositeTask : compositeTasks) {
            if (compositeTask.getChildCount() < 2) {
                compositeTask.getParent().replaceChild(compositeTask, compositeTask.getChildren());
            }
        }
    }



//    -----------------------------------------------------------------------------------------------------------------
//    ----- CLEANING --------------------------------------------------------------------------------------------------
//    -----------------------------------------------------------------------------------------------------------------

    // TODO One method calling all other helpers (private)
    public static void clean(TempTask root) {
        int lastSize;
        do {
            lastSize = getSize(root);
            removeFollowingTasksOfAlwaysSuccessfulTasks(root);
            removeEmptyAndSingleChildCompositeTasks(root);
        } while (getSize(root) < lastSize);
    }

    public static void removeFollowingTasksOfAlwaysSuccessfulTasks(TempTask root) {
        ArrayList<TempSelector> selectors = getTasks(root, true, TempSelector.class);

        for (TempSelector selector : selectors) {
            ArrayList<TempTask> uncheckedChildren = new ArrayList<>(selector.getChildren());
            while (!uncheckedChildren.isEmpty()) {
                if (uncheckedChildren.remove(0) instanceof TempAlwaysSuccessfulTask) {
                    selector.removeChildren(uncheckedChildren);
                    break;
                }
            }
        }
    }
}
