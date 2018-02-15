package experiments.experiment1.tasks.temp;

import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.LeafTasks.TempVariableLeafTask;
import core.BtreeAlt.TempTask;
import experiments.experiment1.tasks.IsApproachingTask;

import static core.util.SystemUtil.random;

public class TempIsApproachingTask extends TempVariableLeafTask {

    private double degreeLimit;

    public TempIsApproachingTask() {
        super("Is approaching");
        randomiseDegreeLimit();
    }

    public TempIsApproachingTask(double degreeLimit) {
        super("Is approaching");
        setDegreeLimit(degreeLimit);
    }

    private void randomiseDegreeLimit() {
        setDegreeLimit(1 + (random.nextDouble() * 44));
    }

    private void setDegreeLimit(double degreeLimit) {
        this.degreeLimit = degreeLimit;
        this.setDisplayName("Is approaching [" + String.format("%.2f", degreeLimit) + "°]");
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
    public TempTask cloneTask() {
        return new TempIsApproachingTask(degreeLimit);
    }

    @Override
    public Task instantiateTask() {
        return new IsApproachingTask(degreeLimit);
    }
}
