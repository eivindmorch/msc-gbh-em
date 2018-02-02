package core.model.btree;

import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.utils.Array;
import core.model.btree.task.VariableLeafTask;
import core.model.btree.task.unit.WaitTask;
import experiments.experiment1.model.btree.task.unit.followerunit.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class BehaviorTreeUtilTest {

    @Test
    void getTasksIncludingRoot() {
        Task waitTask = new WaitTask();
        Task moveToTargetTask1 = new MoveToTargetTask();
        Task moveToTargetTask2 = new MoveToTargetTask();
        Task isApproachingTask = new IsApproachingTask();
        Task followTargetTask = new FollowTargetTask();
        Task isWithinTask = new IsWithinTask();

        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
        Sequence rootSequence = new Sequence(selector, sequence, moveToTargetTask2);

        HashSet<Task> taskSet = new HashSet<>();
        taskSet.add(waitTask);
        taskSet.add(moveToTargetTask1);
        taskSet.add(moveToTargetTask2);
        taskSet.add(isApproachingTask);
        taskSet.add(followTargetTask);
        taskSet.add(isWithinTask);
        taskSet.add(sequence);
        taskSet.add(selector);
        taskSet.add(rootSequence);

        ArrayList<Task> utilTaskList = new ArrayList<>(BehaviorTreeUtil.getTasks(rootSequence, true, Task.class));

        assertEquals(9, utilTaskList.size());
        assertEquals(taskSet, new HashSet<>(utilTaskList));
    }

    @Test
    void getTasksExcludingRoot() {
        Task waitTask = new WaitTask();
        Task moveToTargetTask1 = new MoveToTargetTask();
        Task moveToTargetTask2 = new MoveToTargetTask();
        Task isApproachingTask = new IsApproachingTask();
        Task followTargetTask = new FollowTargetTask();
        Task isWithinTask = new IsWithinTask();

        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
        Sequence rootSequence = new Sequence(selector, sequence, moveToTargetTask2);

        HashSet<Task> taskSet = new HashSet<>();
        taskSet.add(waitTask);
        taskSet.add(moveToTargetTask1);
        taskSet.add(moveToTargetTask2);
        taskSet.add(isApproachingTask);
        taskSet.add(followTargetTask);
        taskSet.add(isWithinTask);
        taskSet.add(sequence);
        taskSet.add(selector);

        ArrayList<Task> utilTaskList = new ArrayList<>(BehaviorTreeUtil.getTasks(rootSequence, false, Task.class));

        assertEquals(8, utilTaskList.size());
        assertEquals(taskSet, new HashSet<>(utilTaskList));
    }

    @Test
    void getCompositeTasks() {
        Task waitTask = new WaitTask();
        Task moveToTargetTask1 = new MoveToTargetTask();
        Task moveToTargetTask2 = new MoveToTargetTask();
        Task isApproachingTask = new IsApproachingTask();
        Task followTargetTask = new FollowTargetTask();
        Task isWithinTask = new IsWithinTask();

        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
        Sequence rootSequence = new Sequence(selector, sequence, moveToTargetTask2);

        HashSet<Task> taskSet = new HashSet<>();
        taskSet.add(sequence);
        taskSet.add(selector);
        taskSet.add(rootSequence);

        ArrayList<Task> utilTaskList = new ArrayList<>(BehaviorTreeUtil.getTasks(rootSequence, true, BranchTask.class));

        assertEquals(3, utilTaskList.size());
        assertEquals(taskSet, new HashSet<>(utilTaskList));
    }

    @Test
    void getVariableLeafTask() {
        Task waitTask = new WaitTask();
        Task moveToTargetTask1 = new MoveToTargetTask();
        Task moveToTargetTask2 = new MoveToTargetTask();
        Task isApproachingTask = new IsApproachingTask();
        Task followTargetTask = new FollowTargetTask();
        Task isWithinTask = new IsWithinTask();
        Task turnToTargetTask = new TurnToTargetTask();

        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
        Sequence rootSequence = new Sequence(selector, sequence, moveToTargetTask2, turnToTargetTask);

        HashSet<Task> taskSet = new HashSet<>();
        taskSet.add(waitTask);
        taskSet.add(moveToTargetTask1);
        taskSet.add(moveToTargetTask2);
        taskSet.add(isApproachingTask);
        taskSet.add(followTargetTask);
        taskSet.add(isWithinTask);

        ArrayList<Task> utilTaskList = new ArrayList<>(BehaviorTreeUtil.getTasks(rootSequence, true, VariableLeafTask.class));

        assertEquals(6, utilTaskList.size());
        assertEquals(taskSet, new HashSet<>(utilTaskList));
    }

    @Test
    void getSize() {
        Task waitTask = new WaitTask();
        Task moveToTargetTask1 = new MoveToTargetTask();
        Task moveToTargetTask2 = new MoveToTargetTask();
        Task isApproachingTask = new IsApproachingTask();
        Task followTargetTask = new FollowTargetTask();
        Task isWithinTask = new IsWithinTask();
        Task turnToTargetTask = new TurnToTargetTask();

        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
        Sequence rootSequence = new Sequence(selector, sequence, moveToTargetTask2, turnToTargetTask);

        assertEquals(10, BehaviorTreeUtil.getSize(rootSequence));
    }

    @Test
    void areEqualTrees() {
        assertTrue(BehaviorTreeUtil.areEqualTrees(generateTree1(), generateTree1()));
        assertFalse(BehaviorTreeUtil.areEqualTrees(generateTree1(), generateTree2()));
    }

    private Task generateTree1() {
        Task waitTask = new WaitTask(1);
        Task moveToTargetTask1 = new MoveToTargetTask(4);
        Task moveToTargetTask2 = new MoveToTargetTask(5);
        Task isApproachingTask = new IsApproachingTask(20);
        Task followTargetTask = new FollowTargetTask(1);
        Task isWithinTask = new IsWithinTask(42);
        Task turnToTargetTask = new TurnToTargetTask();

        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
        return new Sequence<>(selector, sequence, moveToTargetTask2, turnToTargetTask);
    }

    private Task generateTree2() {
        Task waitTask = new WaitTask(3);
        Task moveToTargetTask = new MoveToTargetTask(2);
        Task followTargetTask = new FollowTargetTask(7);
        Task isWithinTask = new IsWithinTask(60);
        Task turnToTargetTask = new TurnToTargetTask();

        Selector selector1= new Selector<>(moveToTargetTask);
        Selector selector2 = new Selector<>(followTargetTask, isWithinTask, waitTask);
        return new Sequence<>(selector1, selector2, turnToTargetTask);
    }

    @Test
    void insertTaskFirstIndex() {
        assertTrue(compareManualInsertionAndInsertTaskWithSpecificIndex(0));
    }

    @Test
    void insertTaskMiddleIndex() {
        assertTrue(compareManualInsertionAndInsertTaskWithSpecificIndex(1));
    }

    @Test
    void insertTaskLastIndex() {
        assertTrue(compareManualInsertionAndInsertTaskWithSpecificIndex(2));
    }

    private boolean compareManualInsertionAndInsertTaskWithSpecificIndex(int index) {
        Task waitTask = new WaitTask(1);
        Task moveToTargetTask1 = new MoveToTargetTask(4);
        Task moveToTargetTask2 = new MoveToTargetTask(5);
        Task isApproachingTask = new IsApproachingTask(20);
        Task followTargetTask = new FollowTargetTask(1);
        Task isWithinTask = new IsWithinTask(42);
        Task turnToTargetTask = new TurnToTargetTask();
        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
        Task root1 = new Sequence<>(selector, sequence, moveToTargetTask2, turnToTargetTask);

        Selector insertionSelector = new Selector(new MoveToTargetTask(2), new MoveToTargetTask(5));

        Task root1WithInsertedTask = BehaviorTreeUtil.insertTask(root1, sequence, index, insertionSelector);

        Array<Task> taskArray = new Array<>();
        taskArray.add(moveToTargetTask1);
        taskArray.add(isApproachingTask);

        taskArray.insert(index, insertionSelector);
        Task root2 = new Sequence<>(selector, new Sequence(taskArray), moveToTargetTask2, turnToTargetTask);
        return BehaviorTreeUtil.areEqualTrees(root1WithInsertedTask, root2);
    }



}