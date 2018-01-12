package model.btree;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import model.btree.task.unit.Wait;
import model.btree.task.unit.experiment1.followerunit.IsApproaching;
import model.btree.task.unit.experiment1.followerunit.IsCloseEnough;
import model.btree.task.unit.experiment1.followerunit.Move;
import model.btree.task.unit.experiment1.followerunit.TurnToHeading;
import unit.Unit;

public class GenBehaviorTree<T extends Unit> extends com.badlogic.gdx.ai.btree.BehaviorTree<Blackboard<T>> {

    public GenBehaviorTree(Task<Blackboard<T>> task, Blackboard<T> blackboard) {
        super(task);
        super.setObject(blackboard);
    }

    public static GenBehaviorTree generateRandomTree() {
        return generateTestTree();
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
        Task<Blackboard<T>> root = this.getChild(0);
        Task<Blackboard<T>> newRoot =
                cloneBehaviorTreeAndInsertSubtree(root, null, null, 0);
        return new GenBehaviorTree(newRoot, this.getObject().clone());
    }

    public GenBehaviorTree cloneAndInsertChild(
                Task<Blackboard<T>> insertParent,
                Task<Blackboard<T>> insertChild, int insertIndex
        ) {
        Task<Blackboard<T>> root = this.getChild(0);
        Task<Blackboard<T>> newRoot =
                cloneBehaviorTreeAndInsertSubtree(root, insertParent, insertChild, insertIndex);
        return new GenBehaviorTree(newRoot, this.getObject().clone());
    }

    // TODO Mutations:
    // cloneAndRemoveChild
    // cloneAndFlipTwoChildren

    private Task<Blackboard<T>> cloneBehaviorTreeAndInsertSubtree(
                Task<Blackboard<T>> root,
                Task<Blackboard<T>> insertParent,
                Task<Blackboard<T>> subtreeRoot,
                int insertIndex
        ) {
        Task<Blackboard<T>> newRoot = instantiateTaskObject(root);
        if (insertIndex < 0 || (root == insertParent && insertIndex > root.getChildCount())) {
            throw new IllegalArgumentException("Invalid insertion index: " + insertIndex);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            if (root == insertParent && i == insertIndex) {
                newRoot.addChild(subtreeRoot);
            }
            Task<Blackboard<T>> child =
                    cloneBehaviorTreeAndInsertSubtree(root.getChild(i), insertParent, subtreeRoot, insertIndex);
            newRoot.addChild(child);
        }
        if (root == insertParent && root.getChildCount() == insertIndex) {
            newRoot.addChild(subtreeRoot);
        }
        return newRoot;
    }

    private Task<Blackboard<T>> instantiateTaskObject(Task<Blackboard<T>> task) {
        Task<Blackboard<T>> newTask = null;
        try {
            //noinspection unchecked
            newTask = task.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return newTask;
    }

}
