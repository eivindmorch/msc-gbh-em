package core.btree.tasks.modular.template.leaf;


public abstract class VariableLeafTask extends LeafTask {

    public abstract void randomiseVariables();

    public abstract void randomiseRandomVariable();

    @Override
    public abstract boolean structurallyEquals(Object o);

}
