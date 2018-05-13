package core.btree.tasks.modular.template.composite;

import com.badlogic.gdx.utils.Array;
import core.btree.tasks.modular.template.Task;

import java.util.*;

public abstract class CompositeTask extends Task {

    public void addChild(Task child) {
        this.children.add(child);
        setThisAsParentFor(child);
    }

    public void addChildren(List<Task> children) {
        this.children.addAll(children);
        setThisAsParentFor(children);
    }

    public void insertChild(int index, Task child) {
        this.children.add(index, child);
        setThisAsParentFor(child);
    }

    public void insertChildren(int index, List<Task> children) {
        this.children.addAll(index, children);
        setThisAsParentFor(children);
    }

    public boolean removeChild(Task child) {
        removeThisAsParentFor(child);
        return children.remove(child);
    }

    public void removeChildren(Collection<Task> children) {
        removeThisAsParentFor(children);
        this.children.removeAll(children);
    }

    public void removeAllChildren() {
        removeThisAsParentFor(this.children);
        this.children = new ArrayList<>();
    }

    public void replaceChild(Task childToReplace, Task newChild) {
        this.children.set(children.indexOf(childToReplace), newChild);
        removeThisAsParentFor(childToReplace);
        setThisAsParentFor(newChild);
    }

    public void replaceChild(Task childToReplace, List<Task> newChildren) {
        int index = this.children.indexOf(childToReplace);

        removeChild(childToReplace);
        insertChildren(index, newChildren);

        removeThisAsParentFor(childToReplace);
        setThisAsParentFor(newChildren);
    }

    public void swapChildrenPositions(int index1, int index2) {
        Collections.swap(children, index1, index2);
    }

    public void shuffleChildren() {
        Collections.shuffle(children);
    }

    private void setThisAsParentFor(Task... children) {
        setThisAsParentFor(Arrays.asList(children));
    }

    private void setThisAsParentFor(List<Task> children) {
        for (Task child : children) {
            child.setParent(this);
        }
    }

    private void removeThisAsParentFor(Task... children) {
        removeThisAsParentFor(Arrays.asList(children));
    }

    private void removeThisAsParentFor(Collection<Task> children) {
        for (Task child : children) {
            child.setParent(null);
        }
    }

    Array instantiateChildren() {
        Array<com.badlogic.gdx.ai.btree.Task> array = new Array<>();
        for (Task child : children) {
            array.add(child.instantiateTask());
        }
        return array;
    }
}
