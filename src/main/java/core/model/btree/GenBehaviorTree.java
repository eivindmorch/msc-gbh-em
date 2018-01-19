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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenBehaviorTree<U extends Unit> extends com.badlogic.gdx.ai.btree.BehaviorTree<Blackboard<U>> {

    private static Random random = new Random();

    public GenBehaviorTree(Task<Blackboard<U>> task, Blackboard<U> blackboard) {
        super(task);
        super.setObject(blackboard);
    }

    public static <U extends Unit> GenBehaviorTree generateRandomTree(Class<U> unitClass)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<? extends Class<? extends Task<? extends Blackboard>>> availableLeafTasks =
                UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableLeafTasks();

        List<Class<? extends BranchTask>> availableCompositeTasks =
                UnitTypeInfo.getUnitInfoFromUnitClass(unitClass).getAvailableCompositeTasks();
        Task subtree = generateRandomSubTree(
                unitClass,
                availableLeafTasks,
                availableCompositeTasks,
                1
        );
        return new GenBehaviorTree(subtree, new Blackboard<U>(null));
    }

    private static Task generateRandomSubTree(
            Class<? extends Unit> unitClass,
            List<? extends Class<? extends Task<? extends Blackboard>>> availableLeafTasks,
            List<Class<? extends BranchTask>> availableCompositeTasks,
            double probabilityForComposite)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Array<Task> children = new Array<>();

        double probabilityForChild = 1;
        while (random.nextDouble() < probabilityForChild) {
            if (random.nextDouble() < probabilityForComposite) {
                children.add(
                        generateRandomSubTree(
                            unitClass,
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
        // TODO Replace with randomisation
        Sequence waitAndTurnToSequence = new Sequence(new Wait(), new TurnToHeading());
        Selector shouldMoveSelector = new Selector(new IsApproaching(15), new IsCloseEnough(5));
        Sequence shouldNotMoveSequence = new Sequence(shouldMoveSelector, waitAndTurnToSequence);
        Selector waitOrMoveSelector = new Selector(shouldNotMoveSequence, new Move());
        return new GenBehaviorTree(waitOrMoveSelector, new Blackboard<>(null));
    }

    @Override
    public GenBehaviorTree clone() {
        Task<Blackboard<U>> root = this.getChild(0);
        Task<Blackboard<U>> newRoot =
                cloneBehaviorTreeAndInsertSubtree(root, null, null, 0);
        return new GenBehaviorTree(newRoot, this.getObject().clone());
    }

    public GenBehaviorTree cloneAndInsertChild(
            Task<Blackboard<U>> insertParent,
            Task<Blackboard<U>> insertChild, int insertIndex
        ) {
        Task<Blackboard<U>> root = this.getChild(0);
        Task<Blackboard<U>> newRoot =
                cloneBehaviorTreeAndInsertSubtree(root, insertParent, insertChild, insertIndex);
        return new GenBehaviorTree(newRoot, this.getObject().clone());
    }

    // TODO Mutations:
    // cloneAndRemoveChild
    // cloneAndFlipTwoChildren

    private Task<Blackboard<U>> cloneBehaviorTreeAndInsertSubtree(
                Task<Blackboard<U>> root,
                Task<Blackboard<U>> insertParent,
                Task<Blackboard<U>> subtreeRoot,
                int insertIndex
        ) {
        Task<Blackboard<U>> newRoot = instantiateTaskObject(root);
        if (insertIndex < 0 || (root == insertParent && insertIndex > root.getChildCount())) {
            throw new IllegalArgumentException("Invalid insertion index: " + insertIndex);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            if (root == insertParent && i == insertIndex) {
                newRoot.addChild(subtreeRoot);
            }
            Task<Blackboard<U>> child =
                    cloneBehaviorTreeAndInsertSubtree(root.getChild(i), insertParent, subtreeRoot, insertIndex);
            newRoot.addChild(child);
        }
        if (root == insertParent && root.getChildCount() == insertIndex) {
            newRoot.addChild(subtreeRoot);
        }
        return newRoot;
    }

    private Task<Blackboard<U>> instantiateTaskObject(Task<Blackboard<U>> task) {
        Task<Blackboard<U>> newTask = null;
        try {
            //noinspection unchecked
            newTask = task.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return newTask;
    }

}
