package training.btree;

import com.badlogic.gdx.ai.btree.Task;

public class BehaviorTree extends com.badlogic.gdx.ai.btree.BehaviorTree {

    public BehaviorTree(Task task) {
        super(task);
    }

    @Override
    public BehaviorTree clone() {
        Task root = this.getChild(0);
        Task newRoot =  cloneBehaviorTreeAndInsertSubtree(root, null, null, 0);
        return new BehaviorTree(newRoot);
    }

    public BehaviorTree cloneAndInsertChild(Task insertParent, Task insertChild, int insertIndex) {
        Task root = this.getChild(0);
        Task newRoot =  cloneBehaviorTreeAndInsertSubtree(root, insertParent, insertChild, insertIndex);
        return new BehaviorTree(newRoot);
    }

    private Task cloneBehaviorTreeAndInsertSubtree(Task root, Task insertParent, Task subtreeRoot, int insertIndex) {
        Task newRoot = instantiateTaskObject(root);
        if (insertIndex < 0 || (root == insertParent && insertIndex > root.getChildCount())) {
            throw new IllegalArgumentException("Invalid insertion index: " + insertIndex);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            if (root == insertParent && i == insertIndex) {
                newRoot.addChild(subtreeRoot);
            }
            Task child = cloneBehaviorTreeAndInsertSubtree(root.getChild(i), insertParent, subtreeRoot, insertIndex);
            newRoot.addChild(child);
        }
        if (root == insertParent && root.getChildCount() == insertIndex) {
            newRoot.addChild(subtreeRoot);
        }
        return newRoot;
    }

    private Task instantiateTaskObject(Task task) {
        Task newTask = null;
        try {
            newTask = task.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return newTask;
    }

}
