package core.model.btree;

import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Decorator;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.utils.Array;
import core.model.btree.task.unit.Wait;
import core.unit.UnitTypeInfo;
import experiments.experiment1.model.btree.task.unit.followerunit.*;
import core.unit.Unit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

// TODO Doc all methods
public abstract class BehaviorTreeUtil {

    private static Random random = new Random();

    public static Task generateRandomTree(Class<? extends Unit> unitClass)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Class<? extends Task>> availableLeafTasks =
                UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableLeafTasks();

        List<Class<? extends BranchTask>> availableCompositeTasks =
                UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableCompositeTasks();
        // TODO Decorators
        Task subtree = generateRandomTree(availableLeafTasks, availableCompositeTasks,1);
        return subtree;
    }

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
        return removeEmptyAndSingleChildCompositeTasks(constructor.newInstance(children));
    }

    public static Task clone(Task root) {
        Task newRoot = insertTask(root, null, 0, null);
        return newRoot;
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
        Task newRoot = instantiateTaskObject(root);
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
    public static Task removeTask(Task root, Task taskToRemove) {
        Task newRoot = instantiateTaskObject(root);
        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = root.getChild(i);
            if (child == taskToRemove) {
                continue;
            }
            newRoot.addChild(removeTask(child, taskToRemove));
        }
        return newRoot;
    }

    /**
     * Replaces a subtree in an already existing behavior tree.
     * @param root the root {@link Task} of the complete behavior tree that is to be edited
     * @param taskToReplace the root {@link Task} of the behavior tree to be replaced. Can not be the same as {@code root}.
     * @param newTask the root {@link Task} of the behavior tree that is to replace {@code taskToReplace}
     * @return the root {@link Task} of the resulting behavior tree
     */
    public static Task replaceTask(Task root, Task taskToReplace, Task newTask) {
        Task newRoot = instantiateTaskObject(root);
        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = root.getChild(i);
            if (child == taskToReplace) {
                newRoot.addChild(newTask);
            } else {
                newRoot.addChild(replaceTask(root.getChild(i), taskToReplace, newTask));
            }
        }
        return newRoot;
    }

    /**
     * Flips the positions of two subtrees in an already existing behavior tree. Do NOT insert two subtrees where one
     * is a the descendant of the other.
     * @param root the root {@link Task} of the complete behavior tree that is to be edited
     * @param subtree1 the root {@link Task} of the first subtree to flip
     * @param subtree2 the root {@link Task} of the second subtree to flip
     * @return the root {@link Task} of the resulting behavior tree
     */
    public static Task flipTwoTasks(Task root, Task subtree1, Task subtree2) {
        Task newRoot = instantiateTaskObject(root);
        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = root.getChild(i);
            if (child == subtree1) {
                newRoot.addChild(subtree2);
            } else if (child == subtree2) {
                newRoot.addChild(subtree1);
            } else {
                newRoot.addChild(flipTwoTasks(root.getChild(i), subtree1, subtree2));
            }
        }
        return newRoot;
    }

    /**
     * Selects a random composite task ({@link BranchTask}) with a specified minimum number of children from an already
     * existing behavior tree, excluding the root {@link Task}.
     * @param root the root {@link Task} of the behavior tree that is to be searched
     * @param minimumNumberOfChildren the minimum number of children the selected composite task ({@link BranchTask}) can have
     * @return the randomly selected composite task ({@link BranchTask}). {@code Null} if none was found.
     */
    public static Task getRandomCompositeTask(Task root, int minimumNumberOfChildren) {
        ArrayList<Task> subtreeRoots = new ArrayList<>();

        Stack<Task> stack = new Stack<>();
        stack.add(root);

        while (!stack.empty()) {
            Task currentRoot = stack.pop();
            for (int i = 0; i < currentRoot.getChildCount(); i++) {
                Task child = currentRoot.getChild(i);
                if (child instanceof BranchTask) {
                    stack.add(child);
                    if (child.getChildCount() >= minimumNumberOfChildren) {
                        subtreeRoots.add(child);
                    }
                }
            }
        }
        if (subtreeRoots.size() > 0) {
            return subtreeRoots.get(random.nextInt(subtreeRoots.size()));
        }
        return null;
    }

    /**
     * Selects a random {@link Task} from an already existing behavior tree, excluding the root {@link Task}.
     * @param root the root {@link Task} of the behavior tree that is to be searched
     * @return the randomly selected {@link Task}
     */
    public static Task getRandomTask(Task root) {
        ArrayList<Task> subtreeRoots = new ArrayList<>();

        Stack<Task> stack = new Stack<>();
        stack.add(root);

        while (!stack.empty()) {
            Task currentRoot = stack.pop();
            for (int i = 0; i < currentRoot.getChildCount(); i++) {
                Task child = currentRoot.getChild(i);
                stack.add(child);
                subtreeRoots.add(child);
            }
        }
        return subtreeRoots.get(random.nextInt(subtreeRoots.size()));
    }

    /**
     * Removes all composite tasks ({@link BranchTask}) with less than two children (no functional value), excluding
     * the root {@link Task}.
     * If you want to change the behavior of a single {@link Task}, you should use a {@link Decorator}
     * @param root the root {@link Task} of the behavior tree that is to be cleaned
     * @return the root {@link Task} of the resulting behavior tree
     */
    public static Task removeEmptyAndSingleChildCompositeTasks(Task root) {
        Task newRoot = instantiateTaskObject(root);
        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = root.getChild(i);
            if (child instanceof BranchTask) {
                if (child.getChildCount() == 1) {
                    newRoot.addChild(removeEmptyAndSingleChildCompositeTasks(child.getChild(0)));
                    continue;
                } else if (child.getChildCount() == 0) {
                    continue;
                }
            }
            newRoot.addChild(removeEmptyAndSingleChildCompositeTasks(child));
        }
        return newRoot;
    }

    private static Task instantiateTaskObject(Task task) {
        Task newTask = null;
        try {
            //noinspection unchecked
            newTask = task.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return newTask;
    }

    public static Task crossover(Task task1, Task task2) {
        Task parent1RandomSubtreeRoot = BehaviorTreeUtil.getRandomTask(task1);
        Task parent2RandomSubtreeRoot = BehaviorTreeUtil.getRandomTask(task2);
        Task child = BehaviorTreeUtil.replaceTask(task1, parent1RandomSubtreeRoot, parent2RandomSubtreeRoot);
        return child;
    }

    /**
     *
     * @param root
     * @param unitClass the class of the unit the behavior tree is to be used for
     * @return
     */
    public static Task mutate(Task root, Class<? extends Unit> unitClass) {
        Task result;

        // Add
        double randValue = random.nextDouble();
        if (randValue < 0.333) {
            System.out.println("ADD");
            Task randomRoot = BehaviorTreeUtil.getRandomCompositeTask(root, 0);
            if (randomRoot == null) {
                result =  BehaviorTreeUtil.clone(root);
            } else {
                try {
                    Task randomTree = BehaviorTreeUtil.generateRandomTree(unitClass);
                    result = BehaviorTreeUtil.insertTask(root, randomRoot, random.nextInt(randomRoot.getChildCount() + 1), randomTree);
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    result = BehaviorTreeUtil.clone(root);
                }
            }
        }
        // Remove
        else if (randValue < 0.666) {
            System.out.println("REMOVE");
            Task randomRoot = BehaviorTreeUtil.getRandomTask(root);
            result = BehaviorTreeUtil.removeTask(root, randomRoot);
        }
        // Flip
        else {
            System.out.println("FLIP");
            Task randomRoot = BehaviorTreeUtil.getRandomCompositeTask(root, 2);
            if (randomRoot == null) {
                result =  BehaviorTreeUtil.clone(root);
            } else {
                int numberOfChildren = randomRoot.getChildCount();
                ArrayList<Task> childList = new ArrayList<>(numberOfChildren);
                for (int i = 0; i < numberOfChildren; i++) {
                    childList.add(randomRoot.getChild(i));
                }
                Task child1 = childList.remove(random.nextInt(childList.size()));
                Task child2 = childList.remove(random.nextInt(childList.size()));

                result = BehaviorTreeUtil.flipTwoTasks(root, child1, child2);
            }
        }
        return removeEmptyAndSingleChildCompositeTasks(result);
    }

    // TODO Mutate:
    // Change type of composite task
    // Change type of leaf task

    public static Task generateTestTree() {
        Sequence waitAndTurnToSequence = new Sequence(new Wait(), new TurnToHeading());
        Selector shouldMoveSelector = new Selector(new IsApproaching(), new IsCloseEnough());
        Sequence shouldNotMoveSequence = new Sequence(shouldMoveSelector, waitAndTurnToSequence);
        Selector waitOrMoveSelector = new Selector(shouldNotMoveSequence, new MoveToTarget());
        return waitOrMoveSelector;
    }

}
