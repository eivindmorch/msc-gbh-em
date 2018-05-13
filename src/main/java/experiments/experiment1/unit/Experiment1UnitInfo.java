package experiments.experiment1.unit;

import core.btree.tasks.modular.template.composite.Selector;
import core.btree.tasks.modular.template.composite.Sequence;
import core.btree.tasks.modular.WaitTask;
import core.unit.UnitTypeInfo;
import experiments.experiment1.tasks.modular.IsApproachingTask;
import experiments.experiment1.tasks.modular.IsWithinTask;
import experiments.experiment1.tasks.modular.MoveToTargetTask;
import experiments.experiment1.tasks.modular.TurnToTargetTask;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Experiment1UnitInfo {

    public static void init() {
        UnitTypeInfo.add(
                "Follower", "F", FollowerUnit.class,
                Arrays.asList(
                        MoveToTargetTask.class,
                        WaitTask.class,
                        IsApproachingTask.class,
                        IsWithinTask.class,
                        TurnToTargetTask.class
                ),
                Arrays.asList(
                        Selector.class,
                        Sequence.class
                )
        );
        UnitTypeInfo.add(
                "Wanderer", "W", Experiment1Unit.class, new ArrayList<>(), new ArrayList<>()
        );
    }

}
