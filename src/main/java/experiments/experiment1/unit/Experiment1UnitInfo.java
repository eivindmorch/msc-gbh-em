package experiments.experiment1.unit;

import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import core.model.btree.task.unit.WaitTask;
import core.unit.UnitTypeInfo;
import experiments.experiment1.model.btree.task.unit.followerunit.*;

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
