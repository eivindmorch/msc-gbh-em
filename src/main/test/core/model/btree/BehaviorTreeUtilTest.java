package core.model.btree;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.sun.javaws.exceptions.InvalidArgumentException;
import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.CompositeTasks.TempSelector;
import core.BtreeAlt.CompositeTasks.TempSequence;
import core.BtreeAlt.LeafTasks.TempVariableLeafTask;
import core.BtreeAlt.TempTask;
import core.model.btree.task.unit.WaitTask;
import core.model.btree.task.unit.temp.TempWaitTask;
import core.util.exceptions.NoSuchTaskFoundException;
import core.util.graphing.GraphTab;
import core.util.graphing.Grapher;
import experiments.experiment1.tasks.IsWithinTask;
import experiments.experiment1.tasks.MoveToTargetTask;
import experiments.experiment1.tasks.TurnToTargetTask;
import experiments.experiment1.tasks.temp.TempIsApproachingTask;
import experiments.experiment1.tasks.temp.TempIsWithinTask;
import experiments.experiment1.tasks.temp.TempMoveToTargetTask;
import experiments.experiment1.tasks.temp.TempTurnToTargetTask;
import experiments.experiment1.unit.Experiment1UnitInfo;
import experiments.experiment1.unit.FollowerUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashSet;

import static core.util.SystemUtil.random;
import static core.util.SystemUtil.sleepSeconds;
import static org.junit.jupiter.api.Assertions.*;

class BehaviorTreeUtilTest {

//    @Test
//    void asd() {
//        TempTask tempRoot = new TempSequence(
//                new TempSelector(
//                        new TempMoveToTargetTask()
//                ),
//                new TempSelector(
//                        new TempIsWithinTask(35.2), new TempWaitTask()
//                ),
//                new TempTurnToTargetTask()
//        );
//
//        Task manual = new Sequence(
//                new Selector(
//                        new MoveToTargetTask()
//                ),
//                new Selector(
//                        new IsWithinTask(35.2), new WaitTask()
//                ),
//                new TurnToTargetTask()
//        );
//
//        Grapher.quickGraph("Original", tempRoot);
//        Grapher.quickGraph("Instantiated", tempRoot.instantiateTask());
//        Grapher.quickGraph("Manual", manual);
//        sleepSeconds(100);
//    }

    // TODO Test instantiateTask()
    // TODO Test generateRandomTree
    // TODO More thorough testing of all add/remove/insert/swap methods of TempTask

    @Test
    void getSize() {
        assertEquals(1, new TempMoveToTargetTask().getSize());
        assertEquals(7, generateTree1().getSize());
        assertEquals(9, generateTree2().getSize());
        assertEquals(14, generateTree3().getSize());
        assertEquals(14, generateTree4().getSize());
    }

    @Test
    void getTasksIncludingRoot() {
        TempTask waitTask = new TempWaitTask();
        TempTask moveToTargetTask1 = new TempMoveToTargetTask();
        TempTask moveToTargetTask2 = new TempMoveToTargetTask();
        TempTask isApproachingTask = new TempIsApproachingTask();
        TempTask isWithinTask = new TempIsWithinTask();

        TempSequence sequence = new TempSequence(moveToTargetTask1, isApproachingTask);
        TempSelector selector = new TempSelector(isWithinTask, waitTask);
        TempSequence rootSequence = new TempSequence(selector, sequence, moveToTargetTask2);

        HashSet<TempTask> taskSet = new HashSet<>();
        taskSet.add(waitTask);
        taskSet.add(moveToTargetTask1);
        taskSet.add(moveToTargetTask2);
        taskSet.add(isApproachingTask);
        taskSet.add(isWithinTask);
        taskSet.add(sequence);
        taskSet.add(selector);
        taskSet.add(rootSequence);

        ArrayList<TempTask> taskList = new ArrayList<>(rootSequence.getTasks(true, TempTask.class));

        assertEquals(8, taskList.size());
        assertEquals(taskSet, new HashSet<>(taskList));
    }

    @Test
    void getTasksExcludingRoot() {
        TempTask waitTask = new TempWaitTask();
        TempTask moveToTargetTask1 = new TempMoveToTargetTask();
        TempTask moveToTargetTask2 = new TempMoveToTargetTask();
        TempTask isApproachingTask = new TempIsApproachingTask();
        TempTask isWithinTask = new TempIsWithinTask();

        TempSequence sequence = new TempSequence(moveToTargetTask1, isApproachingTask);
        TempSelector selector = new TempSelector(isWithinTask, waitTask);
        TempSequence rootSequence = new TempSequence(selector, sequence, moveToTargetTask2);

        HashSet<TempTask> taskSet = new HashSet<>();
        taskSet.add(waitTask);
        taskSet.add(moveToTargetTask1);
        taskSet.add(moveToTargetTask2);
        taskSet.add(isApproachingTask);
        taskSet.add(isWithinTask);
        taskSet.add(sequence);
        taskSet.add(selector);

        ArrayList<TempTask> taskList = new ArrayList<>(rootSequence.getTasks(false, TempTask.class));

        assertEquals(7, taskList.size());
        assertEquals(taskSet, new HashSet<>(taskList));
    }

    @Test
    void getCompositeTasks() {
        TempTask waitTask = new TempWaitTask();
        TempTask moveToTargetTask1 = new TempMoveToTargetTask();
        TempTask moveToTargetTask2 = new TempMoveToTargetTask();
        TempTask isApproachingTask = new TempIsApproachingTask();
        TempTask isWithinTask = new TempIsWithinTask();

        TempSequence sequence = new TempSequence(moveToTargetTask1, isApproachingTask);
        TempSelector selector = new TempSelector(isWithinTask, waitTask);
        TempSequence rootSequence = new TempSequence(selector, sequence, moveToTargetTask2);

        HashSet<TempTask> taskSet = new HashSet<>();
        taskSet.add(sequence);
        taskSet.add(selector);
        taskSet.add(rootSequence);

        ArrayList<TempTask> utilTaskList = new ArrayList<>(rootSequence.getTasks(true, TempCompositeTask.class));

        assertEquals(3, utilTaskList.size());
        assertEquals(taskSet, new HashSet<>(utilTaskList));
    }

    @Test
    void getVariableLeafTask() {
        TempTask waitTask = new TempWaitTask();
        TempTask moveToTargetTask1 = new TempMoveToTargetTask();
        TempTask moveToTargetTask2 = new TempMoveToTargetTask();
        TempTask isApproachingTask = new TempIsApproachingTask();
        TempTask isWithinTask = new TempIsWithinTask();

        TempSequence sequence = new TempSequence(moveToTargetTask1, isApproachingTask);
        TempSelector selector = new TempSelector(isWithinTask, waitTask);
        TempSequence rootSequence = new TempSequence(selector, sequence, moveToTargetTask2);

        HashSet<TempTask> taskSet = new HashSet<>();
        taskSet.add(isApproachingTask);
        taskSet.add(isWithinTask);

        ArrayList<TempTask> utilTaskList = new ArrayList<>(rootSequence.getTasks(true, TempVariableLeafTask.class));

        assertEquals(taskSet.size(), utilTaskList.size());
        assertEquals(taskSet, new HashSet<>(utilTaskList));
    }

    @RepeatedTest(1000)
    void getRandomTaskWithMinimumNumberOfChildren() {
        Experiment1UnitInfo.init();
        int minimumNumOfChildren = random.nextInt(5);

        TempTask rootOfRandomTree = null;
        try {

            rootOfRandomTree = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class, 3, 20);
            TempTask randomTask = rootOfRandomTree.getRandomTask(true, TempTask.class, minimumNumOfChildren);

            assertTrue(randomTask.getChildCount() >= minimumNumOfChildren);

        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchTaskFoundException e) {
            ArrayList<TempTask> taskList = rootOfRandomTree.getTasks(true, TempTask.class);
            ArrayList<TempTask> tasksWithEnoughChildren = new ArrayList<>();
            for (TempTask task : taskList) {
                if (task.getChildCount() >= minimumNumOfChildren) {
                    tasksWithEnoughChildren.add(task);
                }
            }
            assertEquals(0, tasksWithEnoughChildren.size());
        }
    }
//
//    @Test
//    void getRandomRemovableTask() {
//        // TODO
//    }
//
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void insertSubtree(int index) {
        TempTask waitTask = new TempWaitTask();
        TempTask moveToTargetTask1 = new TempMoveToTargetTask();
        TempTask moveToTargetTask2 = new TempMoveToTargetTask();
        TempTask isApproachingTask = new TempIsApproachingTask(20);
        TempTask isWithinTask = new TempIsWithinTask(42);
        TempTask turnToTargetTask = new TempTurnToTargetTask();
        TempSequence sequence = new TempSequence(moveToTargetTask1, isApproachingTask);
        TempSelector selector = new TempSelector(isWithinTask, waitTask);
        TempTask rootOfMethodInsertedTree = new TempSequence(selector, sequence, moveToTargetTask2, turnToTargetTask);

        TempSelector selectorToInsert = new TempSelector(new TempMoveToTargetTask(), new TempMoveToTargetTask());
        sequence.insertChild(index, selectorToInsert);

        ArrayList<TempTask> taskArray = new ArrayList<>();
        taskArray.add(moveToTargetTask1);
        taskArray.add(isApproachingTask);

        taskArray.add(index, selectorToInsert);
        TempTask rootOfManuallyInsertedTree = new TempSequence(selector, new TempSequence(taskArray), moveToTargetTask2, turnToTargetTask);

        assertTrue(rootOfMethodInsertedTree.structurallyEquals(rootOfManuallyInsertedTree));


        assertTrue(treeDoesNotContainDuplicateTasks(rootOfMethodInsertedTree));
    }

    @Test
    void removeTask() {
        TempTask waitTask = new TempWaitTask();
        TempTask moveToTargetTask1 = new TempMoveToTargetTask();
        TempTask moveToTargetTask2 = new TempMoveToTargetTask();
        TempTask isApproachingTask = new TempIsApproachingTask(20);
        TempTask isWithinTask = new TempIsWithinTask(42);
        TempTask turnToTargetTask = new TempTurnToTargetTask();
        TempSequence sequence = new TempSequence(moveToTargetTask1, isApproachingTask);
        TempSelector selector = new TempSelector(isWithinTask, waitTask);
        TempTask rootOfOriginalTree = new TempSequence(selector, sequence, moveToTargetTask2, turnToTargetTask);

        sequence.removeFromParent();

        TempSelector selectorWithRemovedTask = new TempSelector(isWithinTask, waitTask);
        TempTask rootOfTreeWithManualRemove1 = new TempSequence(selectorWithRemovedTask, moveToTargetTask2, turnToTargetTask);

        assertTrue(rootOfOriginalTree.structurallyEquals(rootOfTreeWithManualRemove1));


        assertTrue(treeDoesNotContainDuplicateTasks(rootOfTreeWithManualRemove1));
    }

//    @Test
//    void replaceTask() {
//        TempTask waitTask = new TempWaitTask();
//        TempTask moveToTargetTask1 = new TempMoveToTargetTask();
//        TempTask moveToTargetTask2 = new TempMoveToTargetTask();
//        TempTask isApproachingTask = new TempIsApproachingTask(20);
//        TempTask isWithinTask = new TempIsWithinTask(42);
//        TempTask turnToTargetTask = new TempTurnToTargetTask();
//        TempSequence sequence = new TempSequence(moveToTargetTask1, isApproachingTask);
//        TempSelector selector = new TempSelector(isWithinTask, waitTask);
//        TempTask rootOfOriginalTree = new TempSequence(selector, sequence, moveToTargetTask2, turnToTargetTask);
//
//        sequence.getParent().replaceChild(sequence, new TempWaitTask());
//
//        TempSelector selectorWithRemovedTask = new TempSelector(isWithinTask, waitTask);
//        TempTask rootOfTreeWithManualRemove1 = new TempSequence(selectorWithRemovedTask, moveToTargetTask2, turnToTargetTask);
//
//        assertTrue(rootOfOriginalTree.equals(rootOfTreeWithManualRemove1));
//
//        assertTrue(treeDoesNotContainDuplicateTasks(rootOfTreeWithManualRemove1));
//
//        assertTrue(treeDoesNotContainDuplicateTasks(rootOfTreeWithMethodReplace));
//    }

    @Test
    void switchTasks() {

        TempSelector selectorToBeSwitched = new TempSelector(new TempMoveToTargetTask(), new TempWaitTask(), new TempIsWithinTask(15));

        TempTask methodSwitchedRoot = new TempSelector(
                selectorToBeSwitched,
                new TempSequence(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(43.4)
                ),
                new TempSelector(
                        new TempIsWithinTask(10.2), new TempIsApproachingTask(20), new TempMoveToTargetTask(), new TempWaitTask()
                )
        );

        TempTask originalRoot = methodSwitchedRoot.cloneTask();

        selectorToBeSwitched.swapChildrenPositions(0, 2);

        TempTask manuallySwitchedRoot = new TempSelector(
                new TempSelector(
                        new TempIsWithinTask(15), new TempWaitTask(), new TempMoveToTargetTask()
                        ),
                new TempSequence(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(43.4)
                ),
                new TempSelector(
                        new TempIsWithinTask(10.2), new TempIsApproachingTask(20), new TempMoveToTargetTask(), new TempWaitTask()
                )
        );

        assertFalse(originalRoot.structurallyEquals(methodSwitchedRoot));
        assertTrue(methodSwitchedRoot.structurallyEquals(manuallySwitchedRoot));

        assertTrue(treeDoesNotContainDuplicateTasks(methodSwitchedRoot));
    }

    @Test
    void randomiseIndividualTask() {
        // TODO
    }


    @Test
    void cloneTask() {
        TempTask rootOfOriginal = generateTree1();
        TempTask rootOfClone = rootOfOriginal.cloneTask();
        assertTrue(rootOfOriginal.structurallyEquals(rootOfClone));
        assertFalse(rootOfOriginal.equals(rootOfClone));

        assertTrue(treeDoesNotContainDuplicateTasks(rootOfClone));
    }

    @Test
    void structurallyEquals() {
        assertTrue(generateTree1().structurallyEquals(generateTree1()));
        assertFalse(generateTree1().structurallyEquals(generateTree2()));

        assertTrue(new TempIsApproachingTask(10).structurallyEquals(new TempIsApproachingTask(10)));
        assertFalse(new TempIsApproachingTask(10).structurallyEquals(new TempIsApproachingTask(12)));
    }

    @RepeatedTest(1000)
    void insertAndRemoveThoroughTest() {
        Experiment1UnitInfo.init();
        try {
            TempTask originalRoot = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class, 3, 20);

            TempTask editedRoot = originalRoot.cloneTask();
            TempCompositeTask randomCompositeTask = editedRoot.getRandomTask(true, TempCompositeTask.class);

            TempTask insertionRoot = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class, 3, 20);

            randomCompositeTask.insertChild(random.nextInt(randomCompositeTask.getChildCount()), insertionRoot);
            insertionRoot.removeFromParent();

            assertTrue(originalRoot.structurallyEquals(editedRoot));
            assertTrue(treeDoesNotContainDuplicateTasks(editedRoot));

        } catch (NoSuchTaskFoundException | InvalidArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeFollowingTasksOfAlwaysSuccessfulTasks() {
        TempTask root1Method = generateTree3();
        root1Method = TempTask.removeFollowingTasksOfAlwaysSuccessfulTasks(root1Method);

        TempTask root1Manual = new TempSelector(
                new TempSelector(
                        new TempMoveToTargetTask()
                ),
                new TempSelector(
                        new TempIsWithinTask(10.2), new TempIsApproachingTask(20), new TempMoveToTargetTask()
                ),
                new TempSequence(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(43.4)
                )
        );
        assertTrue(root1Method.structurallyEquals(root1Manual));


        TempTask root2Method = generateTree4();
        root2Method = TempTask.removeFollowingTasksOfAlwaysSuccessfulTasks(root2Method);

        TempTask root2Manual = new TempSequence(
                new TempSelector(
                        new TempMoveToTargetTask()
                ),
                new TempSelector(
                        new TempIsWithinTask(10.2), new TempIsApproachingTask(20), new TempMoveToTargetTask()
                ),
                new TempSequence(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(43.4)
                )
        );
        assertTrue(root2Method.structurallyEquals(root2Manual));
    }

    @Test
    void combineNestedCompositesOfSameType() {
        TempTask root1 = new TempSequence(
                new TempSelector(
                        new TempSelector(
                                new TempMoveToTargetTask()
                        )
                ),
                new TempSequence(
                        new TempIsWithinTask(60), new TempWaitTask()
                ),
                new TempTurnToTargetTask()
        );

        TempTask root2 = new TempSequence(
                new TempSelector(
                        new TempMoveToTargetTask()
                ),
                new TempIsWithinTask(60), new TempWaitTask(),
                new TempTurnToTargetTask()
        );

        assertTrue(TempTask.combineNestedCompositesOfSameType(root1).structurallyEquals(root2));


    }



//  ----------------- HELPERS --------------------------------

    // Size 7
    private TempTask generateTree1() {
        return new TempSequence(
                new TempSelector(
                        new TempMoveToTargetTask()
                ),
                new TempSelector(
                        new TempIsWithinTask(60), new TempWaitTask()
                ),
                new TempTurnToTargetTask()
        );
    }

    // Size 9
    private TempTask generateTree2() {
        return new TempSequence(
                new TempSelector(
                        new TempIsWithinTask(42), new TempWaitTask()
                ),
                new TempSequence(
                        new TempMoveToTargetTask(), new TempIsApproachingTask(20)
                ),
                new TempMoveToTargetTask(),
                new TempTurnToTargetTask()
        );
    }

    // Size 14
    private TempTask generateTree3() {
        return new TempSelector(
                new TempSelector(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(15)
                ),
                new TempSelector(
                        new TempIsWithinTask(10.2), new TempIsApproachingTask(20), new TempMoveToTargetTask(), new TempWaitTask()
                ),
                new TempSequence(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(43.4)
                )
        );
    }

    // Size 14
    private TempTask generateTree4() {
        return new TempSequence(
                new TempSelector(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(15)
                ),
                new TempSelector(
                        new TempIsWithinTask(10.2), new TempIsApproachingTask(20), new TempMoveToTargetTask(), new TempWaitTask()
                ),
                new TempSequence(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(43.4)
                )
        );
    }

    private boolean treeDoesNotContainDuplicateTasks(TempTask root) {
        ArrayList<TempTask> taskList = root.getTasks(true, TempTask.class);
        HashSet<TempTask> taskSet = new HashSet<>(taskList);
        return taskList.size() == taskSet.size();
    }


}