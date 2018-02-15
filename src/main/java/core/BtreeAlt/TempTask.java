package core.BtreeAlt;

import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.CompositeTasks.TempSelector;
import core.util.exceptions.NoSuchTaskFoundException;

import java.util.ArrayList;
import java.util.Stack;

import static core.util.SystemUtil.random;

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

    public int getSize() {
        int size = 1;
        for (TempTask child : children) {
            size += child.getSize();
        }
        return size;
    }

    public int getDepth() {
        int maxChildDepth = 0;
        for (TempTask child : children) {
            maxChildDepth = Math.max(maxChildDepth, child.getDepth());
        }
        return 1 + maxChildDepth;
    }

    public <T extends TempTask> ArrayList<T> getTasks(boolean includeRoot, Class<T> taskTypeToSelect) {
        ArrayList<T> tasks = new ArrayList<>();

        if (includeRoot && taskTypeToSelect.isInstance(this)) {
            tasks.add((T) this);
        }
        Stack<TempTask> stack = new Stack<>();
        stack.add(this);

        while (!stack.empty()) {
            TempTask currentRoot = stack.pop();
            stack.addAll(currentRoot.getChildren());

            for (TempTask child : currentRoot.getChildren()) {
                if (taskTypeToSelect.isInstance(child)) {
                    tasks.add((T) child);
                }
            }
        }
        return tasks;
    }

    public <T extends TempTask> T getRandomTask(boolean includeRoot, Class<T> taskTypeToSelect) throws NoSuchTaskFoundException {
        ArrayList<T> tasks = getTasks(includeRoot, taskTypeToSelect);

        if (tasks.size() == 0) {
            throw new NoSuchTaskFoundException();
        }
        return tasks.get(random.nextInt(tasks.size()));
    }

    public <T extends TempTask> T getRandomTask(boolean includeRoot, Class<T> taskTypeToSelect, int minimumNumberOfChildren) throws NoSuchTaskFoundException {
        ArrayList<T> tasks = getTasks(includeRoot, taskTypeToSelect);
        ArrayList<T> selectionTasks = new ArrayList<>();

        for (T task : tasks) {
            if (task.getChildCount() >= minimumNumberOfChildren) {
                selectionTasks.add(task);
            }
        }
        if (selectionTasks.size() == 0) {
            throw new NoSuchTaskFoundException();
        }
        return selectionTasks.get(random.nextInt(selectionTasks.size()));
    }

    public boolean structurallyEquals(Object o) {
        if (!(o instanceof TempTask)) {
            return false;
        }
        TempTask other = (TempTask) o;

        if (!this.getClass().equals(other.getClass()) || this.getChildCount() != other.getChildCount()) {
            return false;
        }
        for (int i = 0; i < this.getChildCount(); i++) {
            if (!this.getChild(i).structurallyEquals(other.getChild(i))) {
                return false;
            }
        }
        return true;
    }

    // TODO One method calling all other helpers (private)
    public void clean() {
        int lastSize;
        do {
            lastSize = getSize();
            removeFollowingTasksOfAlwaysSuccessfulTasks();
            removeEmptyAndSingleChildCompositeTasks();
        } while (getSize() < lastSize);
    }

    public void removeEmptyAndSingleChildCompositeTasks() {
        ArrayList<TempCompositeTask> compositeTasks = getTasks(false, TempCompositeTask.class);

        for (TempCompositeTask compositeTask : compositeTasks) {
            if (compositeTask.getChildCount() < 2) {
                compositeTask.getParent().replaceChild(compositeTask, compositeTask.getChildren());
            }
        }
    }

    public void removeFollowingTasksOfAlwaysSuccessfulTasks() {
        ArrayList<TempSelector> selectors = getTasks(true, TempSelector.class);

        for (TempSelector selector : selectors) {
            ArrayList<TempTask> uncheckedChildren = new ArrayList<>(selector.getChildren());
            while (!uncheckedChildren.isEmpty()) {
                if (uncheckedChildren.remove(0) instanceof TempAlwaysSuccessfulTask) {
                    selector.removeChildren(uncheckedChildren);
                    break;
                }
            }
        }
    }

    // TODO Rename?
    public abstract TempTask cloneTask();

    // TODO Rename
    public abstract Task instantiateTask();
}
