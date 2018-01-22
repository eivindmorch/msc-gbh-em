package core.model.btree;

import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.utils.Array;
import core.model.btree.task.unit.Wait;
import core.unit.UnitTypeInfo;
import experiments.experiment1.model.btree.task.unit.followerunit.IsApproaching;
import experiments.experiment1.model.btree.task.unit.followerunit.IsCloseEnough;
import experiments.experiment1.model.btree.task.unit.followerunit.Move;
import experiments.experiment1.model.btree.task.unit.followerunit.TurnToHeading;
import core.unit.Unit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GenBehaviorTree extends com.badlogic.gdx.ai.btree.BehaviorTree {

    private static Random random = new Random();

    public GenBehaviorTree(Task task) {
        super(task);
    }

    public Task getRoot() {
        return this.getChild(0);
    }

    public static GenBehaviorTree generateRandomTree(Class<? extends Unit> unitClass)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Class<? extends Task>> availableLeafTasks =
                UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableLeafTasks();

        List<Class<? extends BranchTask>> availableCompositeTasks =
                UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableCompositeTasks();
        // TODO Decorators
        Task subtree = generateRandomSubtree(
                availableLeafTasks,
                availableCompositeTasks,
                1
        );
        return new GenBehaviorTree(subtree);
    }

    public static Task generateRandomSubtree(
            List<Class<? extends Task>> availableLeafTasks,
            List<Class<? extends BranchTask>> availableCompositeTasks,
            double probabilityForComposite)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Array<Task> children = new Array<>();

        double probabilityForChild = 1;
        while (random.nextDouble() < probabilityForChild) {
            if (random.nextDouble() < probabilityForComposite) {
                children.add(
                        generateRandomSubtree(
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

    public static GenBehaviorTree generateTestTree() {
        Sequence waitAndTurnToSequence = new Sequence(new Wait(), new TurnToHeading());
        Selector shouldMoveSelector = new Selector(new IsApproaching(15), new IsCloseEnough(5));
        Sequence shouldNotMoveSequence = new Sequence(shouldMoveSelector, waitAndTurnToSequence);
        Selector waitOrMoveSelector = new Selector(shouldNotMoveSequence, new Move());
        return new GenBehaviorTree(waitOrMoveSelector);
    }

    @Override
    public GenBehaviorTree clone() {
        Task root = this.getChild(0);
        Task newRoot =
                cloneBehaviorTreeAndInsertSubtree(root, null, null, 0);
        return new GenBehaviorTree(newRoot);
    }

    /**
     * This method does NOT clone the Blackboard object.
     * @param root
     * @param compositeTaskToInsertChildTo The
     * @param rootOfSubtreeToBeInserted
     * @param childInsertIndex
     * @return
     */
    public static Task cloneBehaviorTreeAndInsertSubtree(
                Task root,
                BranchTask compositeTaskToInsertChildTo,
                Task rootOfSubtreeToBeInserted,
                int childInsertIndex
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
                    cloneBehaviorTreeAndInsertSubtree(root.getChild(i), compositeTaskToInsertChildTo, rootOfSubtreeToBeInserted, childInsertIndex);
            newRoot.addChild(child);
        }
        if (root == compositeTaskToInsertChildTo && root.getChildCount() == childInsertIndex) {
            newRoot.addChild(rootOfSubtreeToBeInserted);
        }
        return newRoot;
    }

    public static Task cloneBehaviorTreeAndDeleteSubtree(Task root, Task taskToDelete) {
        Task newRoot = instantiateTaskObject(root);
        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = root.getChild(i);
            if (child == taskToDelete) {
                continue;
            }
            newRoot.addChild(cloneBehaviorTreeAndDeleteSubtree(child, taskToDelete));
        }
        return newRoot;
    }

    public static Task cloneBehaviorTreeAndReplaceSubtree(Task root, Task taskToReplace, Task newTask) {
        Task newRoot = instantiateTaskObject(root);
        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = root.getChild(i);
            if (child == taskToReplace) {
                newRoot.addChild(newTask);
            } else {
                newRoot.addChild(cloneBehaviorTreeAndReplaceSubtree(root.getChild(i), taskToReplace, newTask));
            }
        }
        return newRoot;
    }

    private static Task instantiateTaskObject(Task task) {
        Task newTask = null;
        try {
            //noinspection unchecked
            System.out.println(task.getClass().getSimpleName());
            newTask = task.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return newTask;
    }

    /**
     * Flips the positions of two subtrees. Do NOT insert two tasks that are subtree og parent of the other.
     * @param root
     * @param subtree1
     * @param subtree2
     * @return
     */
    public static Task cloneBehaviorTreeAndFlipTwoSubtrees(Task root, Task subtree1, Task subtree2) {
        Task newRoot = instantiateTaskObject(root);
        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = root.getChild(i);
            if (child == subtree1) {
                newRoot.addChild(subtree2);
            } else if (child == subtree2) {
                newRoot.addChild(subtree1);
            } else {
                newRoot.addChild(cloneBehaviorTreeAndFlipTwoSubtrees(root.getChild(i), subtree1, subtree2));
            }
        }
        return newRoot;
    }

    public static Task getRandomTask(Task root, boolean onlyCompositeTasks) {
        ArrayList<Task> subtreeRoots = new ArrayList<>();

        Stack<Task> stack = new Stack<>();
        stack.add(root);

        while (!stack.empty()) {
            Task currentRoot = stack.pop();
            for (int i = 0; i < currentRoot.getChildCount(); i++) {
                Task child = currentRoot.getChild(i);
                if (!onlyCompositeTasks || child.getChildCount() > 0) {
                    stack.add(child);
                    subtreeRoots.add(child);
                }
            }
        }
        return subtreeRoots.get(random.nextInt(subtreeRoots.size()));
    }

    public static Task cloneAndRemoveEmptyAndSingleChildBranchTasks(Task root) {
        Task newRoot = instantiateTaskObject(root);
        for (int i = 0; i < root.getChildCount(); i++) {
            Task child = root.getChild(i);
            if (child instanceof BranchTask) {
                if (child.getChildCount() == 1) {
                    newRoot.addChild(cloneAndRemoveEmptyAndSingleChildBranchTasks(child.getChild(0)));
                    continue;
                } else if (child.getChildCount() == 0) {
                    continue;
                }
            }
            newRoot.addChild(cloneAndRemoveEmptyAndSingleChildBranchTasks(child));
        }
        return newRoot;
    }

    public static GenBehaviorTree crossover(GenBehaviorTree parent1, GenBehaviorTree parent2) {
        Task parent1RandomSubtreeRoot = GenBehaviorTree.getRandomTask(parent1, false);
        Task parent2RandomSubtreeRoot = GenBehaviorTree.getRandomTask(parent2, false);
        Task child = GenBehaviorTree.cloneBehaviorTreeAndReplaceSubtree(
                parent1.getRoot(), parent1RandomSubtreeRoot, parent2RandomSubtreeRoot);
        return new GenBehaviorTree(child);
    }

    public static GenBehaviorTree mutate(GenBehaviorTree btree) {
        // TODO
        // Add
//        if (random.nextDouble() < 0.333) {
//            Task randomRoot = GenBehaviorTree.getRandomTask(btree.getRoot(), true);
//
//            GenBehaviorTree.cloneBehaviorTreeAndInsertSubtree(
//                    btree.getRoot(),
//                    randomRoot,
//                    GenBehaviorTree.gene)
//        }
        // Remove
        // Flip
        return btree.clone();
    }

}
