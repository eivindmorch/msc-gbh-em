package experiments.experiment1.tasks.modular;

import core.btree.tasks.modular.template.Task;
import core.btree.tasks.modular.template.leaf.ConditionTask;
import core.btree.tasks.modular.template.leaf.VariableLeafTask;
import experiments.experiment1.tasks.executable.IsWithinTaskExec;

import static core.util.SystemUtil.random;

public class IsWithinTask extends VariableLeafTask implements ConditionTask {

    private double distanceLimit;

    public IsWithinTask() {
        randomiseDistanceLimit();
    }

    public IsWithinTask(double distanceLimit) {
        this.distanceLimit = distanceLimit;
    }

    private void randomiseDistanceLimit() {
        this.distanceLimit = 1 + (random.nextDouble() * 44);
    }

    @Override
    public void randomiseVariables() {
        randomiseDistanceLimit();
    }

    @Override
    public void randomiseRandomVariable() {
        randomiseDistanceLimit();
    }

    @Override
    public boolean structurallyEquals(Object o) {
        return (o instanceof IsWithinTask && this.distanceLimit == ((IsWithinTask) o).distanceLimit);
    }

    @Override
    public String getDisplayName() {
        return "Is within [" + String.format("%.2f", distanceLimit) + "m]";
    }

    @Override
    public Task cloneTask() {
        return new IsWithinTask(distanceLimit);
    }

    @Override
    public com.badlogic.gdx.ai.btree.Task instantiateExecutableTask() {
        return new IsWithinTaskExec(distanceLimit);
    }
}
