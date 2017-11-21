package training.btree;

import com.badlogic.gdx.ai.btree.Task;

public class GenBehaviorTree extends com.badlogic.gdx.ai.btree.BehaviorTree<Blackboard> {

    public GenBehaviorTree(Task<Blackboard> task, Blackboard blackboard) {
        super(task);
        super.setObject(blackboard);
    }

    @Override
    public GenBehaviorTree clone() {
        Task<Blackboard> root = this.getChild(0);
        Task<Blackboard> newRoot =  cloneBehaviorTreeAndInsertSubtree(root, null, null, 0);
        return new GenBehaviorTree(newRoot, this.getObject().clone());
    }

    public GenBehaviorTree cloneAndInsertChild(Task<Blackboard> insertParent, Task<Blackboard> insertChild, int insertIndex) {
        Task<Blackboard> root = this.getChild(0);
        Task<Blackboard> newRoot =  cloneBehaviorTreeAndInsertSubtree(root, insertParent, insertChild, insertIndex);
        return new GenBehaviorTree(newRoot, this.getObject().clone());
    }

    private Task<Blackboard> cloneBehaviorTreeAndInsertSubtree(
            Task<Blackboard> root, Task<Blackboard> insertParent, Task<Blackboard> subtreeRoot, int insertIndex) {
        Task<Blackboard> newRoot = instantiateTaskObject(root);
        if (insertIndex < 0 || (root == insertParent && insertIndex > root.getChildCount())) {
            throw new IllegalArgumentException("Invalid insertion index: " + insertIndex);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            if (root == insertParent && i == insertIndex) {
                newRoot.addChild(subtreeRoot);
            }
            Task<Blackboard> child =
                    cloneBehaviorTreeAndInsertSubtree(root.getChild(i), insertParent, subtreeRoot, insertIndex);
            newRoot.addChild(child);
        }
        if (root == insertParent && root.getChildCount() == insertIndex) {
            newRoot.addChild(subtreeRoot);
        }
        return newRoot;
    }

    private Task<Blackboard> instantiateTaskObject(Task<Blackboard> task) {
        Task<Blackboard> newTask = null;
        try {
            //noinspection unchecked
            newTask = task.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return newTask;
    }

}
