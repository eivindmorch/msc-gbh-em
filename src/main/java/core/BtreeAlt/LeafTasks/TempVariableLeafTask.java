package core.BtreeAlt.LeafTasks;


public abstract class TempVariableLeafTask extends TempLeafTask {

    public abstract void randomiseVariables();

    public abstract void randomiseRandomVariable();

    @Override
    public abstract boolean structurallyEquals(Object o);

}
