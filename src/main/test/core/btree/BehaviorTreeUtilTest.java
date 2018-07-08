package core.btree;

import com.sun.javaws.exceptions.InvalidArgumentException;
import core.btree.tasks.blueprint.WaitTask;
import core.btree.tasks.blueprint.template.Task;
import core.btree.tasks.blueprint.template.composite.CompositeTask;
import core.btree.tasks.blueprint.template.composite.Selector;
import core.btree.tasks.blueprint.template.composite.Sequence;
import core.btree.tasks.blueprint.template.leaf.VariableLeafTask;
import core.util.exceptions.NoSuchTaskFoundException;
import experiments.experiment1.tasks.modular.IsApproachingTask;
import experiments.experiment1.tasks.modular.IsWithinTask;
import experiments.experiment1.tasks.modular.MoveToTargetTask;
import experiments.experiment1.tasks.modular.TurnToTargetTask;
import experiments.experiment1.Experiment1UnitTypeInfoInitialiser;
import experiments.experiment1.units.FollowerUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashSet;

import static core.util.SystemUtil.random;
import static org.junit.jupiter.api.Assertions.*;

class BehaviorTreeUtilTest {

//    @Test
//    void asd() {
//        Task tempRoot = new Sequence(
//                new Selector(
//                        new MoveToTargetTask()
//                ),
//                new Selector(
//                        new IsWithinTask(35.2), new WaitTask()
//                ),
//                new TurnToTargetTask()
//        );
//
//        Task manual = new Sequence(
//                new Selector(
//                        new MoveToTargetTaskExec()
//                ),
//                new Selector(
//                        new IsWithinTaskExec(35.2), new WaitTaskExec()
//                ),
//                new TurnToTargetTaskExec()
//        );
//
//        FrameManager.quickGraph("Original", tempRoot);
//        FrameManager.quickGraph("Instantiated", tempRoot.instantiateExecutableTask());
//        FrameManager.quickGraph("Manual", manual);
//        sleepSeconds(100);
//    }

    // TODO Test instantiateExecutableTask()
    // TODO Test generateRandomTree
    // TODO More thorough testing of all add/remove/insert/swap methods of Task

    @Test
    void getSize() {
        assertEquals(1, new MoveToTargetTask().getSize());
        assertEquals(7, generateTree1().getSize());
        assertEquals(9, generateTree2().getSize());
        assertEquals(14, generateTree3().getSize());
        assertEquals(14, generateTree4().getSize());
    }

    @Test
    void getTasksIncludingRoot() {
        Task waitTask = new WaitTask();
        Task moveToTargetTask1 = new MoveToTargetTask();
        Task moveToTargetTask2 = new MoveToTargetTask();
        Task isApproachingTask = new IsApproachingTask();
        Task isWithinTask = new IsWithinTask();

        Sequence sequence = new Sequence(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector(isWithinTask, waitTask);
        Sequence rootSequence = new Sequence(selector, sequence, moveToTargetTask2);

        HashSet<Task> taskSet = new HashSet<>();
        taskSet.add(waitTask);
        taskSet.add(moveToTargetTask1);
        taskSet.add(moveToTargetTask2);
        taskSet.add(isApproachingTask);
        taskSet.add(isWithinTask);
        taskSet.add(sequence);
        taskSet.add(selector);
        taskSet.add(rootSequence);

        ArrayList<Task> taskList = new ArrayList<>(rootSequence.getTasks(true, Task.class));

        assertEquals(8, taskList.size());
        assertEquals(taskSet, new HashSet<>(taskList));
    }

    @Test
    void getTasksExcludingRoot() {
        Task waitTask = new WaitTask();
        Task moveToTargetTask1 = new MoveToTargetTask();
        Task moveToTargetTask2 = new MoveToTargetTask();
        Task isApproachingTask = new IsApproachingTask();
        Task isWithinTask = new IsWithinTask();

        Sequence sequence = new Sequence(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector(isWithinTask, waitTask);
        Sequence rootSequence = new Sequence(selector, sequence, moveToTargetTask2);

        HashSet<Task> taskSet = new HashSet<>();
        taskSet.add(waitTask);
        taskSet.add(moveToTargetTask1);
        taskSet.add(moveToTargetTask2);
        taskSet.add(isApproachingTask);
        taskSet.add(isWithinTask);
        taskSet.add(sequence);
        taskSet.add(selector);

        ArrayList<Task> taskList = new ArrayList<>(rootSequence.getTasks(false, Task.class));

        assertEquals(7, taskList.size());
        assertEquals(taskSet, new HashSet<>(taskList));
    }

    @Test
    void getCompositeTasks() {
        Task waitTask = new WaitTask();
        Task moveToTargetTask1 = new MoveToTargetTask();
        Task moveToTargetTask2 = new MoveToTargetTask();
        Task isApproachingTask = new IsApproachingTask();
        Task isWithinTask = new IsWithinTask();

        Sequence sequence = new Sequence(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector(isWithinTask, waitTask);
        Sequence rootSequence = new Sequence(selector, sequence, moveToTargetTask2);

        HashSet<Task> taskSet = new HashSet<>();
        taskSet.add(sequence);
        taskSet.add(selector);
        taskSet.add(rootSequence);

        ArrayList<Task> utilTaskList = new ArrayList<>(rootSequence.getTasks(true, CompositeTask.class));

        assertEquals(3, utilTaskList.size());
        assertEquals(taskSet, new HashSet<>(utilTaskList));
    }

    @Test
    void getVariableLeafTask() {
        Task waitTask = new WaitTask();
        Task moveToTargetTask1 = new MoveToTargetTask();
        Task moveToTargetTask2 = new MoveToTargetTask();
        Task isApproachingTask = new IsApproachingTask();
        Task isWithinTask = new IsWithinTask();

        Sequence sequence = new Sequence(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector(isWithinTask, waitTask);
        Sequence rootSequence = new Sequence(selector, sequence, moveToTargetTask2);

        HashSet<Task> taskSet = new HashSet<>();
        taskSet.add(isApproachingTask);
        taskSet.add(isWithinTask);

        ArrayList<Task> utilTaskList = new ArrayList<>(rootSequence.getTasks(true, VariableLeafTask.class));

        assertEquals(taskSet.size(), utilTaskList.size());
        assertEquals(taskSet, new HashSet<>(utilTaskList));
    }

    @RepeatedTest(1000)
    void getRandomTaskWithMinimumNumberOfChildren() {
        (new Experiment1UnitTypeInfoInitialiser()).initUnitTypeInfo();
        int minimumNumOfChildren = random.nextInt(5);

        Task rootOfRandomTree = null;
        try {

            rootOfRandomTree = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class, 3, 20);
            Task randomTask = rootOfRandomTree.getRandomTask(true, Task.class, minimumNumOfChildren);

            assertTrue(randomTask.getChildCount() >= minimumNumOfChildren);

        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchTaskFoundException e) {
            ArrayList<Task> taskList = rootOfRandomTree.getTasks(true, Task.class);
            ArrayList<Task> tasksWithEnoughChildren = new ArrayList<>();
            for (Task task : taskList) {
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
        Task waitTask = new WaitTask();
        Task moveToTargetTask1 = new MoveToTargetTask();
        Task moveToTargetTask2 = new MoveToTargetTask();
        Task isApproachingTask = new IsApproachingTask(20);
        Task isWithinTask = new IsWithinTask(42);
        Task turnToTargetTask = new TurnToTargetTask();
        Sequence sequence = new Sequence(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector(isWithinTask, waitTask);
        Task rootOfMethodInsertedTree = new Sequence(selector, sequence, moveToTargetTask2, turnToTargetTask);

        Selector selectorToInsert = new Selector(new MoveToTargetTask(), new MoveToTargetTask());
        sequence.insertChild(index, selectorToInsert);

        ArrayList<Task> taskArray = new ArrayList<>();
        taskArray.add(moveToTargetTask1);
        taskArray.add(isApproachingTask);

        taskArray.add(index, selectorToInsert);
        Task rootOfManuallyInsertedTree = new Sequence(selector, new Sequence(taskArray), moveToTargetTask2, turnToTargetTask);

        assertTrue(rootOfMethodInsertedTree.structurallyEquals(rootOfManuallyInsertedTree));


        assertTrue(treeDoesNotContainDuplicateTasks(rootOfMethodInsertedTree));
    }

    @Test
    void removeTask() {
        Task waitTask = new WaitTask();
        Task moveToTargetTask1 = new MoveToTargetTask();
        Task moveToTargetTask2 = new MoveToTargetTask();
        Task isApproachingTask = new IsApproachingTask(20);
        Task isWithinTask = new IsWithinTask(42);
        Task turnToTargetTask = new TurnToTargetTask();
        Sequence sequence = new Sequence(moveToTargetTask1, isApproachingTask);
        Selector selector = new Selector(isWithinTask, waitTask);
        Task rootOfOriginalTree = new Sequence(selector, sequence, moveToTargetTask2, turnToTargetTask);

        sequence.removeFromParent();

        Selector selectorWithRemovedTask = new Selector(isWithinTask, waitTask);
        Task rootOfTreeWithManualRemove1 = new Sequence(selectorWithRemovedTask, moveToTargetTask2, turnToTargetTask);

        assertTrue(rootOfOriginalTree.structurallyEquals(rootOfTreeWithManualRemove1));


        assertTrue(treeDoesNotContainDuplicateTasks(rootOfTreeWithManualRemove1));
    }

//    @Test
//    void replaceTask() {
//        Task waitTask = new WaitTask();
//        Task moveToTargetTask1 = new MoveToTargetTask();
//        Task moveToTargetTask2 = new MoveToTargetTask();
//        Task isApproachingTask = new IsApproachingTask(20);
//        Task isWithinTask = new IsWithinTask(42);
//        Task turnToTargetTask = new TurnToTargetTask();
//        Sequence sequence = new Sequence(moveToTargetTask1, isApproachingTask);
//        Selector selector = new Selector(isWithinTask, waitTask);
//        Task rootOfOriginalTree = new Sequence(selector, sequence, moveToTargetTask2, turnToTargetTask);
//
//        sequence.getParent().replaceChild(sequence, new WaitTask());
//
//        Selector selectorWithRemovedTask = new Selector(isWithinTask, waitTask);
//        Task rootOfTreeWithManualRemove1 = new Sequence(selectorWithRemovedTask, moveToTargetTask2, turnToTargetTask);
//
//        assertTrue(rootOfOriginalTree.equals(rootOfTreeWithManualRemove1));
//
//        assertTrue(treeDoesNotContainDuplicateTasks(rootOfTreeWithManualRemove1));
//
//        assertTrue(treeDoesNotContainDuplicateTasks(rootOfTreeWithMethodReplace));
//    }

    @Test
    void switchTasks() {

        Selector selectorToBeSwitched = new Selector(new MoveToTargetTask(), new WaitTask(), new IsWithinTask(15));

        Task methodSwitchedRoot = new Selector(
                selectorToBeSwitched,
                new Sequence(
                        new MoveToTargetTask(), new MoveToTargetTask(), new IsWithinTask(43.4)
                ),
                new Selector(
                        new IsWithinTask(10.2), new IsApproachingTask(20), new MoveToTargetTask(), new WaitTask()
                )
        );

        Task originalRoot = methodSwitchedRoot.cloneTask();

        selectorToBeSwitched.swapChildrenPositions(0, 2);

        Task manuallySwitchedRoot = new Selector(
                new Selector(
                        new IsWithinTask(15), new WaitTask(), new MoveToTargetTask()
                        ),
                new Sequence(
                        new MoveToTargetTask(), new MoveToTargetTask(), new IsWithinTask(43.4)
                ),
                new Selector(
                        new IsWithinTask(10.2), new IsApproachingTask(20), new MoveToTargetTask(), new WaitTask()
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
        Task rootOfOriginal = generateTree1();
        Task rootOfClone = rootOfOriginal.cloneTask();
        assertTrue(rootOfOriginal.structurallyEquals(rootOfClone));
        assertFalse(rootOfOriginal.equals(rootOfClone));

        assertTrue(treeDoesNotContainDuplicateTasks(rootOfClone));
    }

    @Test
    void structurallyEquals() {
        assertTrue(generateTree1().structurallyEquals(generateTree1()));
        assertFalse(generateTree1().structurallyEquals(generateTree2()));

        assertTrue(new IsApproachingTask(10).structurallyEquals(new IsApproachingTask(10)));
        assertFalse(new IsApproachingTask(10).structurallyEquals(new IsApproachingTask(12)));
    }

    @RepeatedTest(1000)
    void insertAndRemoveThoroughTest() {
        (new Experiment1UnitTypeInfoInitialiser()).initUnitTypeInfo();
        try {
            Task originalRoot = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class, 3, 20);

            Task editedRoot = originalRoot.cloneTask();
            CompositeTask randomCompositeTask = editedRoot.getRandomTask(true, CompositeTask.class);

            Task insertionRoot = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class, 3, 20);

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
        Task root1Method = generateTree3();
        root1Method = Task.removeFollowingTasksOfAlwaysSuccessfulTasks(root1Method);

        Task root1Manual = new Selector(
                new Selector(
                        new MoveToTargetTask()
                ),
                new Selector(
                        new IsWithinTask(10.2), new IsApproachingTask(20), new MoveToTargetTask()
                ),
                new Sequence(
                        new MoveToTargetTask(), new MoveToTargetTask(), new IsWithinTask(43.4)
                )
        );
        assertTrue(root1Method.structurallyEquals(root1Manual));


        Task root2Method = generateTree4();
        root2Method = Task.removeFollowingTasksOfAlwaysSuccessfulTasks(root2Method);

        Task root2Manual = new Sequence(
                new Selector(
                        new MoveToTargetTask()
                ),
                new Selector(
                        new IsWithinTask(10.2), new IsApproachingTask(20), new MoveToTargetTask()
                ),
                new Sequence(
                        new MoveToTargetTask(), new MoveToTargetTask(), new IsWithinTask(43.4)
                )
        );
        assertTrue(root2Method.structurallyEquals(root2Manual));
    }

    @Test
    void combineNestedCompositesOfSameType() {
        Task root1 = new Sequence(
                new Selector(
                        new Selector(
                                new MoveToTargetTask()
                        )
                ),
                new Sequence(
                        new IsWithinTask(60), new WaitTask()
                ),
                new TurnToTargetTask()
        );

        Task root2 = new Sequence(
                new Selector(
                        new MoveToTargetTask()
                ),
                new IsWithinTask(60), new WaitTask(),
                new TurnToTargetTask()
        );

        assertTrue(Task.combineNestedCompositesOfSameType(root1).structurallyEquals(root2));


    }



//  ----------------- HELPERS --------------------------------

    // Size 7
    private Task generateTree1() {
        return new Sequence(
                new Selector(
                        new MoveToTargetTask()
                ),
                new Selector(
                        new IsWithinTask(60), new WaitTask()
                ),
                new TurnToTargetTask()
        );
    }

    // Size 9
    private Task generateTree2() {
        return new Sequence(
                new Selector(
                        new IsWithinTask(42), new WaitTask()
                ),
                new Sequence(
                        new MoveToTargetTask(), new IsApproachingTask(20)
                ),
                new MoveToTargetTask(),
                new TurnToTargetTask()
        );
    }

    // Size 14
    private Task generateTree3() {
        return new Selector(
                new Selector(
                        new MoveToTargetTask(), new MoveToTargetTask(), new IsWithinTask(15)
                ),
                new Selector(
                        new IsWithinTask(10.2), new IsApproachingTask(20), new MoveToTargetTask(), new WaitTask()
                ),
                new Sequence(
                        new MoveToTargetTask(), new MoveToTargetTask(), new IsWithinTask(43.4)
                )
        );
    }

    // Size 14
    private Task generateTree4() {
        return new Sequence(
                new Selector(
                        new MoveToTargetTask(), new MoveToTargetTask(), new IsWithinTask(15)
                ),
                new Selector(
                        new IsWithinTask(10.2), new IsApproachingTask(20), new MoveToTargetTask(), new WaitTask()
                ),
                new Sequence(
                        new MoveToTargetTask(), new MoveToTargetTask(), new IsWithinTask(43.4)
                )
        );
    }

    private boolean treeDoesNotContainDuplicateTasks(Task root) {
        ArrayList<Task> taskList = root.getTasks(true, Task.class);
        HashSet<Task> taskSet = new HashSet<>(taskList);
        return taskList.size() == taskSet.size();
    }


}