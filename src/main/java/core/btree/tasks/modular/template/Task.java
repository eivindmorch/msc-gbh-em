package core.btree.tasks.modular.template;

import core.btree.tasks.modular.template.composite.CompositeTask;
import core.btree.tasks.modular.template.composite.Selector;
import core.util.exceptions.NoSuchTaskFoundException;

import java.util.ArrayList;
import java.util.Stack;

import static core.util.SystemUtil.random;

public abstract class Task {

    protected ArrayList<Task> children;
    private CompositeTask parent;

    public Task() {
        this.children = new ArrayList<>();
    }

    public ArrayList<Task> getChildren() {
        return new ArrayList<>(children);
    }

    public int getChildCount() {
        return children.size();
    }

    public Task getChild(int index) {
        return children.get(index);
    }

    public CompositeTask getParent() {
        return parent;
    }

    public void setParent(CompositeTask parent) {
        this.parent = parent;
    }

    public void removeFromParent() {
        parent.removeChild(this);
    }

    public int getSize() {
        int size = 1;
        for (Task child : children) {
            size += child.getSize();
        }
        return size;
    }

    public int getDepth() {
        int maxChildDepth = 0;
        for (Task child : children) {
            maxChildDepth = Math.max(maxChildDepth, child.getDepth());
        }
        return 1 + maxChildDepth;
    }

    public <T extends Task> ArrayList<T> getTasks(boolean includeRoot, Class<T> taskTypeToSelect) {
        ArrayList<T> tasks = new ArrayList<>();

        if (includeRoot && taskTypeToSelect.isInstance(this)) {
            tasks.add((T) this);
        }
        Stack<Task> stack = new Stack<>();
        stack.add(this);

        while (!stack.empty()) {
            Task currentRoot = stack.pop();
            stack.addAll(currentRoot.getChildren());

            for (Task child : currentRoot.getChildren()) {
                if (taskTypeToSelect.isInstance(child)) {
                    tasks.add((T) child);
                }
            }
        }
        return tasks;
    }

    public <T extends Task> T getRandomTask(boolean includeRoot, Class<T> taskTypeToSelect) throws NoSuchTaskFoundException {
        ArrayList<T> tasks = getTasks(includeRoot, taskTypeToSelect);

        if (tasks.size() == 0) {
            throw new NoSuchTaskFoundException();
        }
        return tasks.get(random.nextInt(tasks.size()));
    }

    public <T extends Task> T getRandomTask(boolean includeRoot, Class<T> taskTypeToSelect, int minimumNumberOfChildren) throws NoSuchTaskFoundException {
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
        if (!(o instanceof Task)) {
            return false;
        }
        Task other = (Task) o;

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
    public Task getCleanVersion() {
        Task cleanTask = this.cloneTask();
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

    public static Task removeEmptyAndSingleChildCompositeTasks(Task root) {
        // TODO Fix (does not check root, needs to replace root if root has too frew children)
        Task cleanTask = root.cloneTask();

        ArrayList<CompositeTask> compositeTasks = cleanTask.getTasks(false, CompositeTask.class);
        for (CompositeTask compositeTask : compositeTasks) {
            if (compositeTask.getChildCount() == 1) {
                CompositeTask parent = compositeTask.getParent();
                ArrayList<Task> children = compositeTask.getChildren();
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

    public static Task removeFollowingTasksOfAlwaysSuccessfulTasks(Task root) {
        Task cleanTask = root.cloneTask();
        ArrayList<Selector> selectors = cleanTask.getTasks(true, Selector.class);

        for (Selector selector : selectors) {
            ArrayList<Task> uncheckedChildren = new ArrayList<>(selector.getChildren());
            while (!uncheckedChildren.isEmpty()) {
                if (uncheckedChildren.remove(0) instanceof AlwaysSuccessfulTask) {
                    selector.removeChildren(uncheckedChildren);
                    break;
                }
            }
        }
        return cleanTask;
    }

    public static Task combineNestedCompositesOfSameType(Task root) {
        Task cleanTask = root.cloneTask();
        ArrayList<CompositeTask> compositeTasks = cleanTask.getTasks(true, CompositeTask.class);

        for (CompositeTask compositeTask : compositeTasks) {
            for (Task child : compositeTask.getChildren()) {
                if (compositeTask.getClass().equals(child.getClass())) {
                    compositeTask.replaceChild(child, child.getChildren());
                    ((CompositeTask) child).removeAllChildren();
                }
            }
        }
        return cleanTask;
    }

    public abstract String getDisplayName();

    // TODO Rename?
    public abstract Task cloneTask();

    // TODO Rename
    public abstract com.badlogic.gdx.ai.btree.Task instantiateTask();
}
