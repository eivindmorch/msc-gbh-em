package core.model.btree.genops;

import core.BtreeAlt.TempTask;
import core.util.exceptions.NoSuchTaskFoundException;

public abstract class Crossover {

    public static TempTask crossover(TempTask parent1Root, TempTask parent2Root) {
        TempTask child = parent1Root.cloneTask();
        try {
            TempTask childRandomSubtreeRoot = child.getRandomTask(false, TempTask.class);
            TempTask parent2RandomSubtreeRoot = parent2Root.getRandomTask(true, TempTask.class).cloneTask();

            childRandomSubtreeRoot.getParent().replaceChild(childRandomSubtreeRoot, parent2RandomSubtreeRoot);
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
        }
        return child.getCleanVersion();
    }
}
