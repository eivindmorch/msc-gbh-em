package experiments.experiment1;

import core.btree.tasks.modular.template.composite.Selector;
import core.btree.tasks.modular.template.composite.Sequence;
import core.btree.tasks.modular.WaitTask;
import core.unit.UnitHandler;
import experiments.UnitTypeInfoInitialiser;
import experiments.experiment1.tasks.modular.IsApproachingTask;
import experiments.experiment1.tasks.modular.IsWithinTask;
import experiments.experiment1.tasks.modular.MoveToTargetTask;
import experiments.experiment1.tasks.modular.TurnToTargetTask;
import experiments.experiment1.units.Experiment1Unit;
import experiments.experiment1.units.FollowerUnit;

import java.util.ArrayList;
import java.util.Arrays;

public class Experiment1UnitTypeInfoInitialiser implements UnitTypeInfoInitialiser {

    @Override
    public void initUnitTypeInfo() {
        UnitHandler.addUnitTypeInfo(
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
        UnitHandler.addUnitTypeInfo(
                "Wanderer", "W", Experiment1Unit.class, new ArrayList<>(), new ArrayList<>()
        );
    }
}
