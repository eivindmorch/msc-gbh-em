package core.model.btree;

import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.utils.Array;
import core.model.btree.task.VariableLeafTask;
import core.model.btree.task.unit.WaitTask;
import core.util.exceptions.NoSuchTasksFoundException;
import experiments.experiment1.model.btree.task.unit.followerunit.*;
import experiments.experiment1.unit.Experiment1UnitInfo;
import experiments.experiment1.unit.FollowerUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

import static core.util.SystemUtil.random;
import static org.junit.jupiter.api.Assertions.*;

class BehaviorTreeUtilTest {

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
        assertEquals(1, BehaviorTreeUtil.getSize(new Sequence()));
    }

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

    @RepeatedTest(1000)
    void getRandomTaskWithMinimumNumberOfChildren() {
        int minimumNumOfChildren = random.nextInt(5);

        Task rootOfRandomTree = null;
        try {

            rootOfRandomTree = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);
            Task randomTask = BehaviorTreeUtil.getRandomTask(rootOfRandomTree, true, Task.class, minimumNumOfChildren);

            assertTrue(randomTask.getChildCount() >= minimumNumOfChildren);

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchTasksFoundException e) {
            ArrayList<Task> taskList = BehaviorTreeUtil.getTasks(rootOfRandomTree, true, Task.class);
            ArrayList<Task> tasksWithEnoughChildren = new ArrayList<>();
            for (Task task : taskList) {
                if (task.getChildCount() >= minimumNumOfChildren) {
                    tasksWithEnoughChildren.add(task);
                }
            }
            assertEquals(0, tasksWithEnoughChildren.size());
        }
    }

    @Test
    void getRandomRemovableTask() {
        // TODO
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void insertSubtree(int index) {
        Task waitTask = new WaitTask(1);
        Task moveToTargetTask1 = new MoveToTargetTask(4);
        Task moveToTargetTask2 = new MoveToTargetTask(5);
        Task isApproachingTask = new IsApproachingTask(20);
        Task followTargetTask = new FollowTargetTask(1);
        Task isWithinTask = new IsWithinTask(42);
        Task turnToTargetTask = new TurnToTargetTask();
        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
        Task rootOfOriginalTree = new Sequence<>(selector, sequence, moveToTargetTask2, turnToTargetTask);

        Selector insertionSelector = new Selector(new MoveToTargetTask(2), new MoveToTargetTask(5));

        Task rootOfMethodInsertedTree = BehaviorTreeUtil.insertTask(rootOfOriginalTree, sequence, index, insertionSelector);

        Array<Task> taskArray = new Array<>();
        taskArray.add(moveToTargetTask1);
        taskArray.add(isApproachingTask);

        taskArray.insert(index, insertionSelector);
        Task rootOfManuallyInsertedTree = new Sequence<>(selector, new Sequence(taskArray), moveToTargetTask2, turnToTargetTask);
        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfMethodInsertedTree, rootOfManuallyInsertedTree));


        assertTrue(treeDoesNotContainDuplicateTasks(rootOfMethodInsertedTree));
    }

    @Test
    void removeTask() {
        Task waitTask = new WaitTask(1);
        Task moveToTargetTask1 = new MoveToTargetTask(4);
        Task moveToTargetTask2 = new MoveToTargetTask(5);
        Task isApproachingTask = new IsApproachingTask(20);
        Task followTargetTask = new FollowTargetTask(1);
        Task isWithinTask = new IsWithinTask(42);
        Task turnToTargetTask = new TurnToTargetTask();
        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
        Task rootOfOriginalTree = new Sequence<>(selector, sequence, moveToTargetTask2, turnToTargetTask);

        Task rootOfTreeWithMethodRemove1 = BehaviorTreeUtil.removeTask(rootOfOriginalTree, followTargetTask);
        Task rootOfTreeWithMethodRemove2 = BehaviorTreeUtil.removeTask(rootOfOriginalTree, sequence);

        Selector selectorWithRemovedTask = new Selector(isWithinTask, waitTask);
        Task rootOfTreWithManualRemove1 = new Sequence(selectorWithRemovedTask, sequence, moveToTargetTask2, turnToTargetTask);
        Task rootOfTreWithManualRemove2 = new Sequence(selector, moveToTargetTask2, turnToTargetTask);

        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfTreWithManualRemove1, rootOfTreeWithMethodRemove1));
        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfTreWithManualRemove2, rootOfTreeWithMethodRemove2));


        assertTrue(treeDoesNotContainDuplicateTasks(rootOfTreeWithMethodRemove1));
        assertTrue(treeDoesNotContainDuplicateTasks(rootOfTreeWithMethodRemove2));
    }

    @Test
    void replaceTask() {
        Task waitTask = new WaitTask(1);
        Task moveToTargetTask1 = new MoveToTargetTask(4);
        Task moveToTargetTask2 = new MoveToTargetTask(5);
        Task isApproachingTask = new IsApproachingTask(20);
        Task followTargetTask = new FollowTargetTask(1);
        Task isWithinTask = new IsWithinTask(42);
        Task turnToTargetTask = new TurnToTargetTask();
        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
        Task rootOfOriginalTree = new Sequence<>(selector, sequence, moveToTargetTask2, turnToTargetTask);

        Task rootOfTreeWithMethodReplace = BehaviorTreeUtil.replaceTask(rootOfOriginalTree, sequence, generateTree2());

        Task waitTaskClone = new WaitTask(1);
        Task moveToTargetTask2Clone = new MoveToTargetTask(5);
        Task followTargetTaskClone = new FollowTargetTask(1);
        Task isWithinTaskClone = new IsWithinTask(42);
        Task turnToTargetTaskClone = new TurnToTargetTask();
        Selector selectorClone = new Selector<>(followTargetTaskClone, isWithinTaskClone, waitTaskClone);
        Task rootOfTreeWithManualReplace = new Sequence<>(selectorClone, generateTree2(), moveToTargetTask2Clone, turnToTargetTaskClone);

        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfTreeWithManualReplace, rootOfTreeWithMethodReplace));
        assertFalse(BehaviorTreeUtil.areEqualTrees(rootOfOriginalTree, rootOfTreeWithManualReplace));


        assertTrue(treeDoesNotContainDuplicateTasks(rootOfTreeWithMethodReplace));
    }

    @Test
    void switchTasks() {
        Task waitTask = new WaitTask(1);
        Task moveToTargetTask1 = new MoveToTargetTask(4);
        Task moveToTargetTask2 = new MoveToTargetTask(5);
        Task isApproachingTask = new IsApproachingTask(20);
        Task followTargetTask = new FollowTargetTask(1);
        Task isWithinTask = new IsWithinTask(42);
        Task turnToTargetTask = new TurnToTargetTask();
        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
        Task rootOfOriginalTree = new Sequence<>(selector, sequence, moveToTargetTask2, turnToTargetTask);

        Task rootOfMethodSwitchedTree1 = BehaviorTreeUtil.switchTasks(rootOfOriginalTree, sequence, turnToTargetTask);
        Task rootOfMethodSwitchedTree2 = BehaviorTreeUtil.switchTasks(rootOfOriginalTree, turnToTargetTask, sequence);

        assertFalse(BehaviorTreeUtil.areEqualTrees(rootOfOriginalTree, rootOfMethodSwitchedTree1));
        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfMethodSwitchedTree1, rootOfMethodSwitchedTree2));

        Task waitTaskClone = new WaitTask(1);
        Task moveToTargetTask1Clone = new MoveToTargetTask(4);
        Task moveToTargetTask2Clone = new MoveToTargetTask(5);
        Task isApproachingTaskClone = new IsApproachingTask(20);
        Task followTargetTaskClone = new FollowTargetTask(1);
        Task isWithinTaskClone = new IsWithinTask(42);
        Task turnToTargetTaskClone = new TurnToTargetTask();
        Sequence sequenceClone = new Sequence<>(moveToTargetTask1Clone, isApproachingTaskClone);
        Selector selectorClone = new Selector<>(followTargetTaskClone, isWithinTaskClone, waitTaskClone);
        Task rootOfManuallySwitchedTree = new Sequence<>(selectorClone, turnToTargetTaskClone, moveToTargetTask2Clone, sequenceClone);

        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfMethodSwitchedTree1, rootOfManuallySwitchedTree));


        assertTrue(treeDoesNotContainDuplicateTasks(rootOfMethodSwitchedTree1));
    }

    @Test
    void randomiseIndividualTask() {
        // TODO
    }

    @Test
    void removeEmptyAndSingleChildCompositeTasks() {
        Task waitTask = new WaitTask(1);
        Task moveToTargetTask1 = new MoveToTargetTask(4);
        Task moveToTargetTask2 = new MoveToTargetTask(5);
        Task isApproachingTask = new IsApproachingTask(20);
        Task followTargetTask = new FollowTargetTask(1);
        Task isWithinTask = new IsWithinTask(42);
        Task turnToTargetTask = new TurnToTargetTask();
        Sequence sequence1 = new Sequence<>(moveToTargetTask1, isApproachingTask);
        Selector selector1 = new Selector<>(isWithinTask, followTargetTask, waitTask);
        Selector selector2 = new Selector(selector1);
        Sequence sequence2 = new Sequence(selector2, sequence1, moveToTargetTask2, turnToTargetTask);
        Task rootOfOriginalTree = new Sequence<>(sequence2);

        Task rootOfMethodCleanedTree = BehaviorTreeUtil.removeEmptyAndSingleChildCompositeTasks(rootOfOriginalTree);

        Task rootOfManuallyCleanedTree = new Sequence(selector1, sequence1, moveToTargetTask2, turnToTargetTask);

        assertFalse(BehaviorTreeUtil.areEqualTrees(rootOfManuallyCleanedTree, rootOfOriginalTree));
        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfManuallyCleanedTree, rootOfMethodCleanedTree));


        assertTrue(treeDoesNotContainDuplicateTasks(rootOfMethodCleanedTree));
    }

    @Test
    void cloneTree() {
        Task rootOfOriginal = generateTree1();
        Task rootOfClone = BehaviorTreeUtil.cloneTree(rootOfOriginal);
        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfOriginal, rootOfClone));


        assertTrue(treeDoesNotContainDuplicateTasks(rootOfClone));
    }

    @Test
    void areEqualTrees() {
        assertTrue(BehaviorTreeUtil.areEqualTrees(generateTree1(), generateTree1()));
        assertFalse(BehaviorTreeUtil.areEqualTrees(generateTree1(), generateTree2()));
    }

    @RepeatedTest(1000)
    void insertAndRemoveThoroughTest() {
        Experiment1UnitInfo.init();
        try {
            Task root = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);
            Task randomCompositeTask = BehaviorTreeUtil.getRandomTask(root, true, BranchTask.class);

            Task insertionRoot = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);

            Task rootWithInsertion = BehaviorTreeUtil.insertTask(root, randomCompositeTask, random.nextInt(randomCompositeTask.getChildCount()), insertionRoot);
            Task rootWithRemovedInsertion = BehaviorTreeUtil.removeTask(rootWithInsertion, insertionRoot);

            assertTrue(BehaviorTreeUtil.areEqualTrees(root, rootWithRemovedInsertion));


            assertTrue(treeDoesNotContainDuplicateTasks(rootWithRemovedInsertion));

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException | NoSuchTasksFoundException e) {
            e.printStackTrace();
        }
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

    private boolean treeDoesNotContainDuplicateTasks(Task root) {
        ArrayList<Task> taskList = BehaviorTreeUtil.getTasks(root, true, Task.class);
        HashSet<Task> taskSet = new HashSet<>(taskList);
        return taskList.size() == taskSet.size();
    }

}