package experiments.experiment1.tasks.temp;

import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.LeafTasks.TempConditionTask;
import core.BtreeAlt.LeafTasks.TempVariableLeafTask;
import core.BtreeAlt.TempTask;
import experiments.experiment1.tasks.IsApproachingTask;

import static core.util.SystemUtil.random;

public class TempIsApproachingTask extends TempVariableLeafTask implements TempConditionTask {

    private double degreeLimit;

    public TempIsApproachingTask() {
        randomiseDegreeLimit();
    }

    public TempIsApproachingTask(double degreeLimit) {
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
        return (o instanceof TempIsApproachingTask && this.degreeLimit == ((TempIsApproachingTask) o).degreeLimit);
    }

    @Override
    public String getDisplayName() {
        return "Is approaching [" + String.format("%.2f", degreeLimit) + "°]";
    }

    @Override
    public TempTask cloneTask() {
        return new TempIsApproachingTask(degreeLimit);
    }

    @Override
    public Task instantiateTask() {
        return new IsApproachingTask(degreeLimit);
    }
}
