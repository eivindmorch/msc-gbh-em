package core.btree.tasks.blueprint.template.leaf;


public abstract class VariableLeafTask extends LeafTask {

    public abstract void randomiseVariables();

    public abstract void randomiseRandomVariable();

    @Override
    public abstract boolean structurallyEquals(Object o);

}
