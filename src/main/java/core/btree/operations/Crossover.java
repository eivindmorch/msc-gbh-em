package core.btree.operations;

import core.btree.tasks.modular.template.Task;
import core.util.exceptions.NoSuchTaskFoundException;

public abstract class Crossover {

    public static Task crossover(Task parent1Root, Task parent2Root) {
        Task child = parent1Root.cloneTask();
        try {
            Task childRandomSubtreeRoot = child.getRandomTask(false, Task.class);
            Task parent2RandomSubtreeRoot = parent2Root.getRandomTask(true, Task.class).cloneTask();

            childRandomSubtreeRoot.getParent().replaceChild(childRandomSubtreeRoot, parent2RandomSubtreeRoot);
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
        }
        return child.getCleanVersion();
    }
}
