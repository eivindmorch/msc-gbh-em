package core.model.btree.task;


import com.badlogic.gdx.ai.btree.LeafTask;
import core.model.btree.Blackboard;

public abstract class VariableLeafTask <B extends Blackboard> extends LeafTask<B> {

    public abstract void randomiseVariables();

    @Override
    public abstract boolean equals(Object obj);

}
