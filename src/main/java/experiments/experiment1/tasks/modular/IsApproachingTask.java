package experiments.experiment1.tasks.modular;

import core.btree.tasks.modular.template.Task;
import core.btree.tasks.modular.template.leaf.ConditionTask;
import core.btree.tasks.modular.template.leaf.VariableLeafTask;
import experiments.experiment1.tasks.executable.IsApproachingTaskExec;

import static core.util.SystemUtil.random;

public class IsApproachingTask extends VariableLeafTask implements ConditionTask {

    private double degreeLimit;

    public IsApproachingTask() {
        randomiseDegreeLimit();
    }

    public IsApproachingTask(double degreeLimit) {
        this.degreeLimit = degreeLimit;
    }

    private void randomiseDegreeLimit() {
        this.degreeLimit = 1 + (random.nextDouble() * 44);
    }

    @Override
    public void randomiseVariables() {
        randomiseDegreeLimit();
    }

    @Override
    public void randomiseRandomVariable() {
        randomiseDegreeLimit();
    }

    @Override
    public boolean structurallyEquals(Object o) {
        return (o instanceof IsApproachingTask && this.degreeLimit == ((IsApproachingTask) o).degreeLimit);
    }

    @Override
    public String getDisplayName() {
        return "Is approaching [" + String.format("%.2f", degreeLimit) + "°]";
    }

    @Override
    public Task cloneTask() {
        return new IsApproachingTask(degreeLimit);
    }

    @Override
    public com.badlogic.gdx.ai.btree.Task instantiateTask() {
        return new IsApproachingTaskExec(degreeLimit);
    }
}
