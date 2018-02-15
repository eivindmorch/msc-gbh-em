package core.BtreeAlt.LeafTasks;


public abstract class TempVariableLeafTask extends TempLeafTask {

    public TempVariableLeafTask(String displayName) {
        super(displayName);
    }

    public abstract void randomiseVariables();

    public abstract void randomiseRandomVariable();

}
