package experiments.experiment1.model.btree.task.unit;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.unit.Unit;

import java.util.HashMap;

public abstract class UnitTaskMap {

    public static HashMap<Class<? extends Unit>, Task<Blackboard>> taskMap = new HashMap<>();

    static {
        // TODO Loop all tasks, recursively add to map
        // "Unit" = units tasks
        // "FollowerUnit" = units + follower tasks
    }


}
