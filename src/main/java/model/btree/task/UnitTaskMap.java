package model.btree.task;

import com.badlogic.gdx.ai.btree.Task;
import model.btree.Blackboard;
import unit.Unit;

import java.util.HashMap;

public abstract class UnitTaskMap {

    public static HashMap<Class<? extends Unit>, Task<Blackboard>> taskMap = new HashMap<>();

    static {
        // TODO Loop all tasks, recursively add to map
        // "Unit" = unit tasks
        // "FollowerUnit" = unit + follower tasks
    }


}
