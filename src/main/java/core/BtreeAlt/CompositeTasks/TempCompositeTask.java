package core.BtreeAlt.CompositeTasks;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.utils.Array;
import core.BtreeAlt.TempTask;

import java.util.*;

public abstract class TempCompositeTask extends TempTask {

    public TempCompositeTask(String displayName) {
        super(displayName);
    }

    public void addChild(TempTask child) {
        this.children.add(child);
        setThisAsParentFor(child);
    }

    public void addChildren(List<TempTask> children) {
        this.children.addAll(children);
        setThisAsParentFor(children);
    }

    public void insertChild(int index, TempTask child) {
        this.children.add(index, child);
        setThisAsParentFor(child);
    }

    public void insertChildren(int index, List<TempTask> children) {
        this.children.addAll(index, children);
        setThisAsParentFor(children);
    }

    public boolean removeChild(TempTask child) {
        removeThisAsParentFor(child);
        return children.remove(child);
    }

    public void removeChildren(Collection<TempTask> children) {
        removeThisAsParentFor(children);
        this.children.removeAll(children);
    }

    public void removeAllChildren() {
        removeThisAsParentFor(this.children);
        this.children = new ArrayList<>();
    }

    public void replaceChild(TempTask childToReplace, TempTask newChild) {
        this.children.set(children.indexOf(childToReplace), newChild);
        removeThisAsParentFor(childToReplace);
        setThisAsParentFor(newChild);
    }

    public void replaceChild(TempTask childToReplace, List<TempTask> children) {
        int index = this.children.indexOf(childToReplace);

        removeChild(childToReplace);
        insertChildren(index, children);

        removeThisAsParentFor(childToReplace);
        setThisAsParentFor(children);
    }

    public void swapChildrenPositions(int index1, int index2) {
        Collections.swap(children, index1, index2);
    }

    public void shuffleChildren() {
        Collections.shuffle(children);
    }

    private void setThisAsParentFor(TempTask... children) {
        setThisAsParentFor(Arrays.asList(children));
    }

    private void setThisAsParentFor(List<TempTask> children) {
        for (TempTask child : children) {
            child.setParent(this);
        }
    }

    private void removeThisAsParentFor(TempTask... children) {
        removeThisAsParentFor(Arrays.asList(children));
    }

    private void removeThisAsParentFor(Collection<TempTask> children) {
        for (TempTask child : children) {
            child.setParent(null);
        }
    }

    Array instantiateChildren() {
        Array<Task> array = new Array<>();
        for (TempTask child : children) {
            array.add(child.instantiateTask());
        }
        return array;
    }
}
