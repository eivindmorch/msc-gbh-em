package experiments.experiment1;

import core.model.btree.task.unit.Wait;
import core.unit.Unit;
import core.unit.UnitTypeInfo;
import experiments.experiment1.model.btree.task.unit.followerunit.IsApproaching;
import experiments.experiment1.model.btree.task.unit.followerunit.IsCloseEnough;
import experiments.experiment1.model.btree.task.unit.followerunit.Move;
import experiments.experiment1.model.btree.task.unit.followerunit.TurnToHeading;
import experiments.experiment1.unit.FollowerUnit;

import java.util.ArrayList;
import java.util.Arrays;

public class Experiment1UnitInfo {

    static {
        UnitTypeInfo.add(
                "Follower", "F", FollowerUnit.class,
                Arrays.asList(
                        Move.class,
                        Wait.class,
                        IsApproaching.class,
                        IsCloseEnough.class,
                        TurnToHeading.class
                )
        );
        UnitTypeInfo.add(
                "Wanderer", "W", Unit.class, new ArrayList<>()
        );
        System.out.println(UnitTypeInfo.getUnitInfoFromSymbol("F"));
        System.out.println(UnitTypeInfo.getUnitInfoFromSymbol("W"));
    }

}
