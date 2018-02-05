package core.model.btree.genops;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.util.exceptions.NoSuchTaskFoundException;

public abstract class Crossover {

    public static Task crossover(Task parent1Root, Task parent2Root) {
        try {
            Task parent1RandomSubtreeRoot = BehaviorTreeUtil.getRandomTask(parent1Root, false, Task.class);
            Task parent2RandomSubtreeRoot = BehaviorTreeUtil.getRandomTask(parent2Root, true, Task.class);
            Task child = BehaviorTreeUtil.replaceTask(parent1Root, parent1RandomSubtreeRoot, parent2RandomSubtreeRoot);
            return BehaviorTreeUtil.removeEmptyAndSingleChildCompositeTasks(child);
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            return BehaviorTreeUtil.cloneTree(parent1Root);
        }
    }
}
