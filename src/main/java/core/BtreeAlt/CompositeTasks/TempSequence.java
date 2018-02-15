package core.BtreeAlt.CompositeTasks;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import core.BtreeAlt.TempTask;

import java.util.Arrays;
import java.util.List;

public class TempSequence extends TempCompositeTask {

    public TempSequence() {
        super("->");
    }

    public TempSequence(TempTask... children) {
        this(Arrays.asList(children));
    }

    public TempSequence(List<TempTask> children) {
        this();
        addChildren(children);
    }

    @Override
    public TempTask cloneTask() {
        TempSequence tempSequence = new TempSequence();
        for (TempTask child : getChildren()) {
            tempSequence.addChild(child.cloneTask());
        }
        return tempSequence;
    }

    @Override
    public Task instantiateTask() {
        return new Sequence(instantiateChildren());
    }
}
