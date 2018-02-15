package core.model.btree.genops;

import core.BtreeAlt.TempTask;
import core.model.btree.BehaviorTreeUtil;
import core.util.exceptions.NoSuchTaskFoundException;

public abstract class Crossover {

    public static TempTask crossover(TempTask parent1Root, TempTask parent2Root) {
        TempTask child = parent1Root.cloneTask();
        try {
            TempTask childRandomSubtreeRoot = BehaviorTreeUtil.getRandomTask(child, false, TempTask.class);
            TempTask parent2RandomSubtreeRoot = BehaviorTreeUtil.getRandomTask(parent2Root, true, TempTask.class).cloneTask();

            childRandomSubtreeRoot.getParent().replaceChild(childRandomSubtreeRoot, parent2RandomSubtreeRoot);
            BehaviorTreeUtil.clean(child);
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
        }
        return child;
    }
}
