package core.BtreeAlt;

import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.CompositeTasks.TempCompositeTask;

import java.util.ArrayList;

public abstract class TempTask {

    private String displayName;
    protected ArrayList<TempTask> children;
    private TempCompositeTask parent;

    public TempTask(String displayName) {
        this.displayName = displayName;
        this.children = new ArrayList<>();
    }

    public String getDisplayName() {
        return displayName;
    }

    protected void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<TempTask> getChildren() {
        return children;
    }

    public int getChildCount() {
        return children.size();
    }

    public TempTask getChild(int index) {
        return children.get(index);
    }

    public TempCompositeTask getParent() {
        return parent;
    }

    public void setParent(TempCompositeTask parent) {
        this.parent = parent;
    }

    public void removeFromParent() {
        parent.removeChild(this);
    }

    public boolean isFunctionallyEqual(TempTask o) {
        if (!this.getClass().equals(o.getClass()) || this.getChildCount() != o.getChildCount()) {
            return false;
        }
        for (int i = 0; i < this.getChildCount(); i++) {
            if (!this.getChild(i).isFunctionallyEqual(o.getChild(i))) {
                return false;
            }
        }
        return true;

    }

    public abstract TempTask cloneTask();

    public abstract Task instantiateTask();
}
