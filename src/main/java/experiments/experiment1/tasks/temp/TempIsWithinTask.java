package experiments.experiment1.tasks.temp;

import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.LeafTasks.TempVariableLeafTask;
import core.BtreeAlt.TempTask;
import experiments.experiment1.tasks.IsWithinTask;

import static core.util.SystemUtil.random;

public class TempIsWithinTask extends TempVariableLeafTask {

    private double distanceLimit;

    public TempIsWithinTask() {
        randomiseDistanceLimit();
    }

    public TempIsWithinTask(double distanceLimit) {
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
        return (o instanceof TempIsWithinTask && this.distanceLimit == ((TempIsWithinTask) o).distanceLimit);
    }

    @Override
    public String getDisplayName() {
        return "Is within [" + String.format("%.2f", distanceLimit) + "°]";
    }

    @Override
    public TempTask cloneTask() {
        return new TempIsWithinTask(distanceLimit);
    }

    @Override
    public Task instantiateTask() {
        return new IsWithinTask(distanceLimit);
    }
}
