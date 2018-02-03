package core.model.btree;

import com.badlogic.gdx.ai.btree.*;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import core.model.btree.task.VariableLeafTask;
import core.unit.UnitTypeInfo;
import core.util.exceptions.NoAvailableTaskClassException;
import core.util.exceptions.NoSuchTaskFoundException;
import experiments.experiment1.model.btree.task.unit.followerunit.*;
import core.unit.Unit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static core.util.SystemUtil.random;

// TODO Doc all methods
@SuppressWarnings("WeakerAccess")
public abstract class BehaviorTreeUtil {

    public static Task generateRandomTree(Class<? extends Unit> unitClass)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Class<? extends Task>> availableLeafTasks =
                UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableLeafTasks();

        List<Class<? extends BranchTask>> availableCompositeTasks =
                UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableCompositeTasks();
        // TODO Decorators
        Task subtree = generateRandomTree(availableLeafTasks, availableCompositeTasks,1);

        return removeEmptyAndSingleChildCompositeTasks(subtree);
    }

    // TODO Probabilities as arguments
    /**
     * Generates a random behavior tree.
     * @param availableLeafTasks pool of {@link LeafTask} that can be used in the tree
     * @param availableCompositeTasks pool of composite tasks ({@link BranchTask}) that can be used in the tree
     * @param probabilityForComposite probability between 0 and 1 of the roots child being a composite tasks ({@link BranchTask})
     * @return the root {@link Task} of the generated behavior tree
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    @SuppressWarnings("JavaDoc")
    private static Task generateRandomTree(
            List<Class<? extends Task>> availableLeafTasks,
            List<Class<? extends BranchTask>> availableCompositeTasks,
            double probabilityForComposite)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Array<Task> children = new Array<>();

        double probabilityForChild = 1;
        while (random.nextDouble() < probabilityForChild) {
            if (random.nextDouble() < probabilityForComposite) {
                children.add(
                        generateRandomTree(
                            availableLeafTasks,
                            availableCompositeTasks,
                            probabilityForComposite * 0.5
                        )
                );
            } else {
                try {
                    children.add(availableLeafTasks.get(random.nextInt(availableLeafTasks.size())).newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            probabilityForChild *= 0.6;
        }
        Constructor<? extends BranchTask> constructor = availableCompositeTasks.get(random.nextInt(availableCompositeTasks.size())).getConstructor(Array.class);
        return constructor.newInstance(children);
    }

    public static Task generateTestTree() {
        Selector shouldMoveSelector = new Selector(new IsApproachingTask(), new IsWithinTask());
        Sequence shouldNotMoveSequence = new Sequence(shouldMoveSelector, new TurnToTargetTask());
        Selector waitOrMoveSelector = new Selector(shouldNotMoveSequence, new MoveToTargetTask());
        return waitOrMoveSelector;
    }

    public static int getSize(Task root) {
        int size = 1;

        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = root.getChild(i);
            size += getSize(child);
        }
        return size;
    }

    /**
     * Gets all tasks of the specified type.
     * @param root the root {@link Task} of the behavior tree that is to be searched
     * @param includeRoot whether to include the root in the selection process
     * @param taskTypeToSelect the type of task to be selected
     * @param <T> the type of {@code taskTypeToSelect}
     * @return a {@link List} of all tasks of type {@code taskTypeToSelect}
     */
    public static <T extends Task> ArrayList<T> getTasks(Task root, boolean includeRoot, Class<T> taskTypeToSelect) {
        ArrayList<T> subtreeRoots = new ArrayList<>();

        if (includeRoot && taskTypeToSelect.isInstance(root)) {
            subtreeRoots.add((T) root);
        }

        Stack<Task> stack = new Stack<>();
        stack.add(root);

        while (!stack.empty()) {
            Task currentRoot = stack.pop();
            for (int i = 0; i < currentRoot.getChildCount(); i++) {
                Task child = currentRoot.getChild(i);
                stack.add(child);
                if (taskTypeToSelect.isInstance(child)) {
                    subtreeRoots.add((T) child);
                }
            }
        }
        return subtreeRoots;
    }

    /**
     * Selects a random {@link Task} of the specified type from an already existing behavior tree.
     * @param root the root {@link Task} of the behavior tree that is to be searched
     * @param includeRoot whether to include the root in the selection process
     * @param taskTypeToSelect the type of task to be selected
     * @return the randomly selected {@link Task}
     * @param <T> the type of {@code taskTypeToSelect}
     * @throws NoSuchTaskFoundException tree does not contain any tasks meeting the specified requirements
     */
    public static <T extends Task> T getRandomTask(Task root, boolean includeRoot, Class<T> taskTypeToSelect) throws NoSuchTaskFoundException {
        ArrayList<T> tasks = getTasks(root, includeRoot, taskTypeToSelect);

        if (tasks.size() == 0) {
            throw new NoSuchTaskFoundException();
        }
        return tasks.get(random.nextInt(tasks.size()));
    }

    /**
     * Selects a random {@link Task} of the specified type and with the specified minimum number of children
     * from an already existing behavior tree.
     * existing behavior tree, excluding the root {@link Task}.
     * @param root the root {@link Task} of the behavior tree that is to be searched
     * @param includeRoot whether to include the root in the selection process
     * @param taskTypeToSelect the type of task to be selected
     * @param minimumNumberOfChildren the minimum number of children the selected composite task ({@link BranchTask}) can have
     * @param <T> the type of {@code taskTypeToSelect}
     * @return the randomly selected composite task ({@link BranchTask}). {@code Null} if none was found.
     * @throws NoSuchTaskFoundException tree does not contain any tasks meeting the specified requirements
     */
    public static <T extends Task> T getRandomTask(Task root, boolean includeRoot, Class<T> taskTypeToSelect, int minimumNumberOfChildren) throws NoSuchTaskFoundException {
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

    /**
     * Selects a random {@link Task} that can be removed from the tree without breaking it
     * @param root the root {@link Task} of the behavior tree that is to be searched
     * @return a {@link Task} that can be removed from the tree without breaking it
     * @throws NoSuchTaskFoundException
     */
    public static Task getRandomRemovableTask(Task root) throws NoSuchTaskFoundException {
        ArrayList<Task> removableTasks = new ArrayList<>();

        Stack<Task> stack = new Stack<>();
        stack.add(root);

        while (!stack.empty()) {
            Task currentRoot = stack.pop();
            if (currentRoot.getChildCount() > 1) {
                removableTasks.addAll(getTasks(currentRoot, false, Task.class));
            } else {
                for (int i = 0; i < currentRoot.getChildCount(); i++) {
                    stack.add(currentRoot.getChild(i));
                }
            }
        }
        if (removableTasks.isEmpty()) {
            throw new NoSuchTaskFoundException();
        }
        return removableTasks.get(random.nextInt(removableTasks.size()));
    }

    /**
     * Inserts a behavior tree to an already existing behavior tree.
     * @param root the root {@link Task} of the complete behavior tree that is to be edited
     * @param compositeTaskToInsertChildTo the composite task ({@link BranchTask}) that the task should be added as a child to
     * @param childInsertIndex the index of where in the child list of @{@code compositeTaskToInsertChildTo} the task should be inserted
     * @param rootOfSubtreeToBeInserted the root {@link Task} of the behavior tree that is to be inserted
     * @return the root {@link Task} of the resulting behavior tree
     */
    public static Task insertTask(
                Task root,
                Task compositeTaskToInsertChildTo,
                int childInsertIndex,
                Task rootOfSubtreeToBeInserted
        ) {
        Task newRoot = cloneIndividualTask(root);
        if (childInsertIndex < 0 || (root == compositeTaskToInsertChildTo && childInsertIndex > root.getChildCount())) {
            throw new IllegalArgumentException("Invalid insertion index: " + childInsertIndex);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            if (root == compositeTaskToInsertChildTo && i == childInsertIndex) {
                newRoot.addChild(rootOfSubtreeToBeInserted);
            }
            Task child =
                    insertTask(root.getChild(i), compositeTaskToInsertChildTo, childInsertIndex, rootOfSubtreeToBeInserted);
            newRoot.addChild(child);
        }
        if (root == compositeTaskToInsertChildTo && root.getChildCount() == childInsertIndex) {
            newRoot.addChild(rootOfSubtreeToBeInserted);
        }
        return newRoot;
    }

    /**
     * Removes a subtree from an already existing behavior tree.
     * @param root the root {@link Task} of the complete behavior tree that is to be edited
     * @param taskToRemove the root {@link Task} of the behavior tree to be removed. Can not be the same as {@code root}.
     * @return the root {@link Task} of the resulting behavior tree
     */
    public static Task removeTask(Task root, Task taskToRemove) throws NoSuchTaskFoundException {
        HashSet<Task> taskSet = new HashSet<>(BehaviorTreeUtil.getTasks(root, true, Task.class));
        if (!taskSet.contains(taskToRemove)) {
            throw new NoSuchTaskFoundException();
        }

        Task newRoot;
        if (root == taskToRemove) {
            return null;
        } else {
            newRoot = cloneIndividualTask(root);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            Task newChild = removeTask(root.getChild(i), taskToRemove);
            if (newChild != null) {
                newRoot.addChild(newChild);
            }
        }
        return newRoot;
    }

    /**
     * Replaces a subtree in an already existing behavior tree.
     * @param root the root {@link Task} of the complete behavior tree that is to be edited
     * @param rootOfSubtreeToBeReplaced the root {@link Task} of the behavior tree to be replaced. Can not be the same as {@code root}.
     * @param rootOfNewSubtree the root {@link Task} of the behavior tree that is to replace {@code rootOfSubtreeToBeReplaced}
     * @return the root {@link Task} of the resulting behavior tree
     */
    public static Task replaceTask(Task root, Task rootOfSubtreeToBeReplaced, Task rootOfNewSubtree) {
        Task newRoot;
        if (root == rootOfSubtreeToBeReplaced) {
            newRoot = cloneTree(rootOfNewSubtree);
        } else {
            newRoot = cloneIndividualTask(root);
            for (int i = 0; i < root.getChildCount(); i++) {
                newRoot.addChild(replaceTask(root.getChild(i), rootOfSubtreeToBeReplaced, rootOfNewSubtree));
            }
        }
        return newRoot;
    }

    /**
     * Switches the positions of two subtrees in an already existing behavior tree. Do NOT insert two subtrees where one
     * is a the descendant of the other.
     * @param root the root {@link Task} of the complete behavior tree that is to be edited
     * @param subtree1 the root {@link Task} of the first subtree to flip
     * @param subtree2 the root {@link Task} of the second subtree to flip
     * @return the root {@link Task} of the resulting behavior tree
     */
    public static Task switchTasks(Task root, Task subtree1, Task subtree2) {
        Task newRoot = cloneIndividualTask(root);

        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = root.getChild(i);
            if (child == subtree1) {
                newRoot.addChild(subtree2);
            } else if (child == subtree2) {
                newRoot.addChild(subtree1);
            } else {
                newRoot.addChild(switchTasks(root.getChild(i), subtree1, subtree2));
            }
        }
        return newRoot;
    }

    /**
     * Changes a specified {@link Task} to a different task of the same type (leaf, composite, decorator, etc.).
     * @param root the root {@link Task} of the behavior tree that contains the task to be randomised
     * @param taskToRandomise the {@link Task} that is to be randomised
     * @param unitClass the class of the unit the behavior tree is to be used for
     * @return the root {@link Task} of the resulting behavior tree
     */
    public static Task randomiseIndividualTask(Task root, Task taskToRandomise, Class<? extends Unit> unitClass) throws NoAvailableTaskClassException {
        Task newRoot = cloneIndividualTask(root);

        ArrayList<Class<? extends Task>> availableTaskClasses = new ArrayList<>();

        if (root == taskToRandomise) {

            // TODO Decorator
            if (taskToRandomise instanceof LeafTask) {
                availableTaskClasses.addAll(UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableLeafTasks());
            } else if (taskToRandomise instanceof BranchTask) {
                availableTaskClasses.addAll(UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableCompositeTasks());
            }
            availableTaskClasses.remove(taskToRandomise.getClass());

            if (availableTaskClasses.isEmpty()) {
                throw new NoAvailableTaskClassException(
                        "No available task classes for " + taskToRandomise.getClass().getSimpleName()
                                + "(" + taskToRandomise.getClass().getTypeName() + " type)"
                );
            }

            try {
                newRoot = availableTaskClasses.get(random.nextInt(availableTaskClasses.size())).newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < root.getChildCount(); i++) {
            newRoot.addChild(randomiseIndividualTask(root.getChild(i), taskToRandomise, unitClass));
        }
        return newRoot;
    }

    /**
     * Removes all composite tasks ({@link BranchTask}) with less than two children (no functional value), excluding
     * the root {@link Task}.
     * If you want to change the behavior of a single {@link Task}, you should use a {@link Decorator}
     * @param root the root {@link Task} of the behavior tree that is to be cleaned
     * @return the root {@link Task} of the resulting behavior tree
     */
    public static Task removeEmptyAndSingleChildCompositeTasks(Task root) {
        Task newRoot = cloneIndividualTask(root);
        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = removeEmptyAndSingleChildCompositeTasksRecursiveHelper(root.getChild(i));
            if (child != null) {
                newRoot.addChild(child);
            }
        }
        if (newRoot.getChildCount() == 1 && newRoot.getChild(0) instanceof BranchTask) {
            return newRoot.getChild(0);
        }
        return newRoot;
    }

    private static Task removeEmptyAndSingleChildCompositeTasksRecursiveHelper(Task root) {
        Task newRoot = cloneIndividualTask(root);
        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = removeEmptyAndSingleChildCompositeTasksRecursiveHelper(root.getChild(i));
            if (child != null) {
                newRoot.addChild(child);
            }
        }
        if (newRoot instanceof BranchTask) {
            if (newRoot.getChildCount() == 0) {
                return null;
            } else if (newRoot.getChildCount() == 1) {
                return newRoot.getChild(0);
            }
        }
        return newRoot;
    }

    public static Task cloneTree(Task root) {
        return root.cloneTask();
    }

    // TODO Handle Decorators
    private static Task cloneIndividualTask(Task task) {
       try {
           if (task instanceof VariableLeafTask) {
               return cloneTree(task);
           }
           return ClassReflection.newInstance(task.getClass());
        } catch (ReflectionException var3) {
            throw new TaskCloneException(var3);
        }
    }

    public static boolean areEqualTrees(Task root1, Task root2) {
        if (areOfSameClassWithSameVariables(root1, root2) && root1.getChildCount() == root2.getChildCount()) {
            for (int i = 0; i < root1.getChildCount(); i++) {
                if (!areEqualTrees(root1.getChild(i), root2.getChild(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean areOfSameClassWithSameVariables(Task task1, Task task2) {
        if (task1.getClass() == task2.getClass()) {
            if (task1 instanceof VariableLeafTask) {
                return task1.equals(task2);
            }
            return true;
        }
        return false;

    }
}
