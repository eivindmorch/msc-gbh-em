package core.model.btree;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import core.model.btree.task.unit.Wait;
import experiments.experiment1.model.btree.task.unit.followerunit.IsApproaching;
import experiments.experiment1.model.btree.task.unit.followerunit.IsCloseEnough;
import experiments.experiment1.model.btree.task.unit.followerunit.Move;
import experiments.experiment1.model.btree.task.unit.followerunit.TurnToHeading;
import core.unit.Unit;

public class GenBehaviorTree<U extends Unit> extends com.badlogic.gdx.ai.btree.BehaviorTree<Blackboard<U>> {

    public GenBehaviorTree(Task<Blackboard<U>> task, Blackboard<U> blackboard) {
        super(task);
        super.setObject(blackboard);
    }

    public static GenBehaviorTree generateRandomTree() {
        // TODO
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
