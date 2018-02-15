package core.BtreeAlt.CompositeTasks;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import core.BtreeAlt.TempTask;

import java.util.Arrays;
import java.util.List;

public class TempSelector extends TempCompositeTask {

    public TempSelector() {
        super("?");
    }

    public TempSelector(TempTask... children) {
        this(Arrays.asList(children));
    }

    public TempSelector(List<TempTask> children) {
        this();
        addChildren(children);
    }

    @Override
    public TempTask cloneTask() {
        TempSelector tempSelector = new TempSelector();
        for (TempTask child : getChildren()) {
            tempSelector.addChild(child.cloneTask());
        }
        return tempSelector;
    }

    @Override
    public Task instantiateTask() {
        return new Selector(instantiateChildren());
    }
}
