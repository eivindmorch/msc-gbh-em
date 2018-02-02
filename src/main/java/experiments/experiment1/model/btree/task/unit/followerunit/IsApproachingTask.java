package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.VariableLeafTask;
import experiments.experiment1.unit.FollowerUnit;

import static core.util.SystemUtil.random;

public class IsApproachingTask extends VariableLeafTask<Blackboard<FollowerUnit>> implements NamedTask {

    private double degreeLimit;
    private String name;

    public IsApproachingTask() {
        randomiseDegreeLimit();
    }

    public IsApproachingTask(double degreeLimit) {
        setDegreeLimit(degreeLimit);
    }

    public IsApproachingTask(IsApproachingTask isApproachingTaskTask) {
        this(isApproachingTaskTask.degreeLimit);
    }

    @Override
    public Status execute() {
        Double otherUnitMovementAngleRelativeToMyPosition =
                getObject().getUnit().getFollowerProcessedDataRow().getTargetMovementAngleRelativeToFollowerPosition();

        if (otherUnitMovementAngleRelativeToMyPosition == null) {
            return Status.FAILED;
        }
        if (otherUnitMovementAngleRelativeToMyPosition < degreeLimit || otherUnitMovementAngleRelativeToMyPosition > 360 - degreeLimit) {
            return Status.SUCCEEDED;
        }
        return Status.FAILED;
    }

    @Override
    protected Task<Blackboard<FollowerUnit>> copyTo(Task<Blackboard<FollowerUnit>> task) {
        return new IsApproachingTask(this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void randomiseVariables() {
        randomiseDegreeLimit();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IsApproachingTask && this.degreeLimit == ((IsApproachingTask) obj).degreeLimit;
    }

    private void randomiseDegreeLimit() {
        setDegreeLimit(1 + (random.nextDouble() * 44));
    }

    private void setDegreeLimit(double degreeLimit) {
        this.degreeLimit = degreeLimit;
        this.name = "Is approaching [" + String.format("%.2f", degreeLimit) + "°]";
    }
}
