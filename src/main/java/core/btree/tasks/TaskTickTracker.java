package core.btree.tasks;



public class TaskTickTracker {

    private int ticksToRun;
    private int currentTick;
    private Status currentStatus;

    public enum Status {
        FIRST, RUNNING, DONE
    }

    public TaskTickTracker(int ticksToRun){
        this.ticksToRun = ticksToRun;
        this.currentStatus = Status.FIRST;
    }

    public void tick() {
        currentTick++;
        if (currentTick <= ticksToRun) {
            currentStatus = Status.RUNNING;
        } else {
            currentStatus = Status.DONE;
            currentTick = 0;
        }
    }

    public Status getCurrentStatus() {
        return currentStatus;
    }

    public void reset() {
        currentTick = 0;
        currentStatus = Status.FIRST;
    }
}