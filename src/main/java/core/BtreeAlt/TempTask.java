package core.BtreeAlt;

import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.CompositeTasks.TempSelector;
import core.util.exceptions.NoSuchTaskFoundException;

import java.util.ArrayList;
import java.util.Stack;

import static core.util.SystemUtil.random;

public abstract class TempTask {

    protected ArrayList<TempTask> children;
    private TempCompositeTask parent;

    public TempTask() {
        this.children = new ArrayList<>();
    }

    public ArrayList<TempTask> getChildren() {
        return new ArrayList<>(children);
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
    public TempTask getCleanVersion() {
        TempTask cleanTask = this.cloneTask();
        int lastSize;
        do {
            lastSize = cleanTask.getSize();

            cleanTask = removeFollowingTasksOfAlwaysSuccessfulTasks(cleanTask);
            cleanTask = removeEmptyAndSingleChildCompositeTasks(cleanTask);

            cleanTask = combineNestedCompositesOfSameType(cleanTask);
            cleanTask = removeEmptyAndSingleChildCompositeTasks(cleanTask);

        } while (cleanTask.getSize() < lastSize);
        return cleanTask;
    }

    public static TempTask removeEmptyAndSingleChildCompositeTasks(TempTask root) {
        // TODO Fix (does not check root, needs to replace root if root has too frew children)
        TempTask cleanTask = root.cloneTask();

        ArrayList<TempCompositeTask> compositeTasks = cleanTask.getTasks(false, TempCompositeTask.class);
        for (TempCompositeTask compositeTask : compositeTasks) {
            if (compositeTask.getChildCount() == 1) {
                TempCompositeTask parent = compositeTask.getParent();
                ArrayList<TempTask> children = compositeTask.getChildren();
                parent.replaceChild(compositeTask, children);

            } else if (compositeTask.getChildCount() == 0) {
                compositeTask.removeFromParent();
            }
        }
        // Remove root if it has only one child
        if (cleanTask.getChildCount() == 1) {
            return cleanTask.getChild(0);
        }
        return cleanTask;
    }

    public static TempTask removeFollowingTasksOfAlwaysSuccessfulTasks(TempTask root) {
        TempTask cleanTask = root.cloneTask();
        ArrayList<TempSelector> selectors = cleanTask.getTasks(true, TempSelector.class);

        for (TempSelector selector : selectors) {
            ArrayList<TempTask> uncheckedChildren = new ArrayList<>(selector.getChildren());
            while (!uncheckedChildren.isEmpty()) {
                if (uncheckedChildren.remove(0) instanceof TempAlwaysSuccessfulTask) {
                    selector.removeChildren(uncheckedChildren);
                    break;
                }
            }
        }
        return cleanTask;
    }

    public static TempTask combineNestedCompositesOfSameType(TempTask root) {
        TempTask cleanTask = root.cloneTask();
        ArrayList<TempCompositeTask> compositeTasks = cleanTask.getTasks(true, TempCompositeTask.class);

        for (TempCompositeTask compositeTask : compositeTasks) {
            for (TempTask child : compositeTask.getChildren()) {
                if (compositeTask.getClass().equals(child.getClass())) {
                    compositeTask.replaceChild(child, child.getChildren());
                    ((TempCompositeTask) child).removeAllChildren();
                }
            }
        }
        return cleanTask;
    }

    public abstract String getDisplayName();

    // TODO Rename?
    public abstract TempTask cloneTask();

    // TODO Rename
    public abstract Task instantiateTask();
}
