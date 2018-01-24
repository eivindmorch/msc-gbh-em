package core.model.btree.task;

import com.badlogic.gdx.ai.btree.Task;

public class TaskTickTracker {

    private int ticksToRun;
    private int currentTick;

    public TaskTickTracker(int ticksToRun){
        this.ticksToRun = ticksToRun;
    }

    public Task.Status tick() {
        if (currentTick < ticksToRun) {
            currentTick++;
            return Task.Status.RUNNING;
        } else {
            currentTick = 0;
            return Task.Status.SUCCEEDED;
        }
    }

    public int getCurrentTick() {
        return currentTick;
    }
}