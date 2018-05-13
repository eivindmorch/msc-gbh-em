package experiments.experiment1.unit;

import core.BtreeAlt.CompositeTasks.TempSelector;
import core.BtreeAlt.CompositeTasks.TempSequence;
import core.btree.task.unit.temp.TempWaitTask;
import core.unit.UnitTypeInfo;
import experiments.experiment1.tasks.temp.TempIsApproachingTask;
import experiments.experiment1.tasks.temp.TempIsWithinTask;
import experiments.experiment1.tasks.temp.TempMoveToTargetTask;
import experiments.experiment1.tasks.temp.TempTurnToTargetTask;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Experiment1UnitInfo {

    public static void init() {
        UnitTypeInfo.add(
                "Follower", "F", FollowerUnit.class,
                Arrays.asList(
                        TempMoveToTargetTask.class,
                        TempWaitTask.class,
                        TempIsApproachingTask.class,
                        TempIsWithinTask.class,
                        TempTurnToTargetTask.class
                ),
                Arrays.asList(
                        TempSelector.class,
                        TempSequence.class
                )
        );
        UnitTypeInfo.add(
                "Wanderer", "W", Experiment1Unit.class, new ArrayList<>(), new ArrayList<>()
        );
    }

}
