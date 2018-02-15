package core.model.btree;

import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.utils.Array;
import com.sun.javaws.exceptions.InvalidArgumentException;
import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.CompositeTasks.TempSelector;
import core.BtreeAlt.CompositeTasks.TempSequence;
import core.BtreeAlt.LeafTasks.TempVariableLeafTask;
import core.BtreeAlt.TempTask;
import core.model.btree.genops.Mutator;
import core.model.btree.genops.mutations.AddRandomSubtreeMutation;
import core.model.btree.genops.mutations.RandomiseVariablesOfRandomVariableTaskMutation;
import core.model.btree.genops.mutations.SwitchPositionsOfRandomSiblingTasksMutation;
import core.model.btree.task.unit.WaitTask;
import core.model.btree.task.unit.temp.TempWaitTask;
import core.training.Chromosome;
import core.util.exceptions.NoSuchTaskFoundException;
import core.util.graphing.GraphFrame;
import core.util.graphing.GraphPanel;
import core.util.graphing.GraphTab;
import core.util.graphing.Grapher;
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

    @Test
    void createTree() {
        Experiment1UnitInfo.init();
        try {
            TempTask root = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class, 5, 5);
//            GraphFrame graphFrame = Grapher.createNewFrame("Asd");
//            GraphTab graphTab = new GraphTab("sdk");
//            graphTab.add(new Chromosome(root));

//            graphTab.add(new Chromosome(root.cloneTask()));
            TempTask newRoot = root.cloneTask();

            new AddRandomSubtreeMutation(1, true).mutate(newRoot, FollowerUnit.class);
//            graphTab.add(new Chromosome(newRoot));

//            graphFrame.addTab(graphTab);
//            graphFrame.display();
//            sleepSeconds(20);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSize() {
        assertEquals(9, BehaviorTreeUtil.getSize(generateTree1()));
        assertEquals(7, BehaviorTreeUtil.getSize(generateTree2()));
        assertEquals(1, BehaviorTreeUtil.getSize(new TempMoveToTargetTask()));
    }

//    @Test
//    void getTasksIncludingRoot() {
//        Task waitTask = new WaitTask();
//        Task moveToTargetTask1 = new MoveToTargetTask();
//        Task moveToTargetTask2 = new MoveToTargetTask();
//        Task isApproachingTask = new IsApproachingTask();
//        Task followTargetTask = new FollowTargetTask();
//        Task isWithinTask = new IsWithinTask();
//
//        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
//        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
//        Sequence rootSequence = new Sequence(selector, sequence, moveToTargetTask2);
//
//        HashSet<Task> taskSet = new HashSet<>();
//        taskSet.add(waitTask);
//        taskSet.add(moveToTargetTask1);
//        taskSet.add(moveToTargetTask2);
//        taskSet.add(isApproachingTask);
//        taskSet.add(followTargetTask);
//        taskSet.add(isWithinTask);
//        taskSet.add(sequence);
//        taskSet.add(selector);
//        taskSet.add(rootSequence);
//
//        ArrayList<Task> utilTaskList = new ArrayList<>(BehaviorTreeUtil.getTasks(rootSequence, true, Task.class));
//
//        assertEquals(9, utilTaskList.size());
//        assertEquals(taskSet, new HashSet<>(utilTaskList));
//    }
//
//    @Test
//    void getTasksExcludingRoot() {
//        Task waitTask = new WaitTask();
//        Task moveToTargetTask1 = new MoveToTargetTask();
//        Task moveToTargetTask2 = new MoveToTargetTask();
//        Task isApproachingTask = new IsApproachingTask();
//        Task followTargetTask = new FollowTargetTask();
//        Task isWithinTask = new IsWithinTask();
//
//        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
//        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
//        Sequence rootSequence = new Sequence(selector, sequence, moveToTargetTask2);
//
//        HashSet<Task> taskSet = new HashSet<>();
//        taskSet.add(waitTask);
//        taskSet.add(moveToTargetTask1);
//        taskSet.add(moveToTargetTask2);
//        taskSet.add(isApproachingTask);
//        taskSet.add(followTargetTask);
//        taskSet.add(isWithinTask);
//        taskSet.add(sequence);
//        taskSet.add(selector);
//
//        ArrayList<Task> utilTaskList = new ArrayList<>(BehaviorTreeUtil.getTasks(rootSequence, false, Task.class));
//
//        assertEquals(8, utilTaskList.size());
//        assertEquals(taskSet, new HashSet<>(utilTaskList));
//    }
//
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

        ArrayList<TempTask> utilTaskList = new ArrayList<>(BehaviorTreeUtil.getTasks(rootSequence, true, TempCompositeTask.class));

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

        ArrayList<TempTask> utilTaskList = new ArrayList<>(BehaviorTreeUtil.getTasks(rootSequence, true, TempVariableLeafTask.class));

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
            TempTask randomTask = BehaviorTreeUtil.getRandomTask(rootOfRandomTree, true, TempTask.class, minimumNumOfChildren);

            assertTrue(randomTask.getChildCount() >= minimumNumOfChildren);

        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchTaskFoundException e) {
            ArrayList<TempTask> taskList = BehaviorTreeUtil.getTasks(rootOfRandomTree, true, TempTask.class);
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

        assertTrue(rootOfMethodInsertedTree.isFunctionallyEqual(rootOfManuallyInsertedTree));

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

        assertTrue(rootOfOriginalTree.isFunctionallyEqual(rootOfTreeWithManualRemove1));

        assertTrue(treeDoesNotContainDuplicateTasks(rootOfTreeWithManualRemove1));

    }
//
//    @Test
//    void replaceTask() {
//        Task waitTask = new WaitTask();
//        Task moveToTargetTask1 = new MoveToTargetTask();
//        Task moveToTargetTask2 = new MoveToTargetTask();
//        Task isApproachingTask = new IsApproachingTask(20);
//        Task followTargetTask = new FollowTargetTask();
//        Task isWithinTask = new IsWithinTask(42);
//        Task turnToTargetTask = new TurnToTargetTask();
//        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
//        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
//        Task rootOfOriginalTree = new Sequence<>(selector, sequence, moveToTargetTask2, turnToTargetTask);
//
//        Task rootOfTreeWithMethodReplace = BehaviorTreeUtil.replaceTask(rootOfOriginalTree, sequence, generateTree2());
//
//        Task waitTaskClone = new WaitTask();
//        Task moveToTargetTask2Clone = new MoveToTargetTask();
//        Task followTargetTaskClone = new FollowTargetTask();
//        Task isWithinTaskClone = new IsWithinTask(42);
//        Task turnToTargetTaskClone = new TurnToTargetTask();
//        Selector selectorClone = new Selector<>(followTargetTaskClone, isWithinTaskClone, waitTaskClone);
//        Task rootOfTreeWithManualReplace = new Sequence<>(selectorClone, generateTree2(), moveToTargetTask2Clone, turnToTargetTaskClone);
//
//        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfTreeWithManualReplace, rootOfTreeWithMethodReplace));
//        assertFalse(BehaviorTreeUtil.areEqualTrees(rootOfOriginalTree, rootOfTreeWithManualReplace));
//
//
//        assertTrue(treeDoesNotContainDuplicateTasks(rootOfTreeWithMethodReplace));
//    }
//
//    @Test
//    void switchTasks() {
//        Task waitTask = new WaitTask();
//        Task moveToTargetTask1 = new MoveToTargetTask();
//        Task moveToTargetTask2 = new MoveToTargetTask();
//        Task isApproachingTask = new IsApproachingTask(20);
//        Task followTargetTask = new FollowTargetTask();
//        Task isWithinTask = new IsWithinTask(42);
//        Task turnToTargetTask = new TurnToTargetTask();
//        Sequence sequence = new Sequence<>(moveToTargetTask1, isApproachingTask);
//        Selector selector = new Selector<>(followTargetTask, isWithinTask, waitTask);
//        Task rootOfOriginalTree = new Sequence<>(selector, sequence, moveToTargetTask2, turnToTargetTask);
//
//        Task rootOfMethodSwitchedTree1 = BehaviorTreeUtil.switchTasks(rootOfOriginalTree, sequence, turnToTargetTask);
//        Task rootOfMethodSwitchedTree2 = BehaviorTreeUtil.switchTasks(rootOfOriginalTree, turnToTargetTask, sequence);
//
//        assertFalse(BehaviorTreeUtil.areEqualTrees(rootOfOriginalTree, rootOfMethodSwitchedTree1));
//        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfMethodSwitchedTree1, rootOfMethodSwitchedTree2));
//
//        Task waitTaskClone = new WaitTask();
//        Task moveToTargetTask1Clone = new MoveToTargetTask();
//        Task moveToTargetTask2Clone = new MoveToTargetTask();
//        Task isApproachingTaskClone = new IsApproachingTask(20);
//        Task followTargetTaskClone = new FollowTargetTask();
//        Task isWithinTaskClone = new IsWithinTask(42);
//        Task turnToTargetTaskClone = new TurnToTargetTask();
//        Sequence sequenceClone = new Sequence<>(moveToTargetTask1Clone, isApproachingTaskClone);
//        Selector selectorClone = new Selector<>(followTargetTaskClone, isWithinTaskClone, waitTaskClone);
//        Task rootOfManuallySwitchedTree = new Sequence<>(selectorClone, turnToTargetTaskClone, moveToTargetTask2Clone, sequenceClone);
//
//        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfMethodSwitchedTree1, rootOfManuallySwitchedTree));
//
//
//        assertTrue(treeDoesNotContainDuplicateTasks(rootOfMethodSwitchedTree1));
//    }
//
//    @Test
//    void randomiseIndividualTask() {
//        // TODO
//    }
//
//    @Test
//    void removeEmptyAndSingleChildCompositeTasks() {
//        Task waitTask = new WaitTask();
//        Task moveToTargetTask1 = new MoveToTargetTask();
//        Task moveToTargetTask2 = new MoveToTargetTask();
//        Task isApproachingTask = new IsApproachingTask(20);
//        Task followTargetTask = new FollowTargetTask();
//        Task isWithinTask = new IsWithinTask(42);
//        Task turnToTargetTask = new TurnToTargetTask();
//        Sequence sequence1 = new Sequence<>(moveToTargetTask1, isApproachingTask);
//        Selector selector1 = new Selector<>(isWithinTask, followTargetTask, waitTask);
//        Selector selector2 = new Selector(selector1);
//        Sequence sequence2 = new Sequence(selector2, sequence1, moveToTargetTask2, turnToTargetTask);
//        Task rootOfOriginalTree = new Sequence<>(sequence2);
//
//        Task rootOfMethodCleanedTree = BehaviorTreeUtil.removeEmptyAndSingleChildCompositeTasks(rootOfOriginalTree);
//
//        Task rootOfManuallyCleanedTree = new Sequence(selector1, sequence1, moveToTargetTask2, turnToTargetTask);
//
//        assertFalse(BehaviorTreeUtil.areEqualTrees(rootOfManuallyCleanedTree, rootOfOriginalTree));
//        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfManuallyCleanedTree, rootOfMethodCleanedTree));
//
//
//        assertTrue(treeDoesNotContainDuplicateTasks(rootOfMethodCleanedTree));
//    }
//
//    @Test
//    void cloneTree() {
//        Task rootOfOriginal = generateTree1();
//        Task rootOfClone = BehaviorTreeUtil.cloneTree(rootOfOriginal);
//        assertTrue(BehaviorTreeUtil.areEqualTrees(rootOfOriginal, rootOfClone));
//
//
//        assertTrue(treeDoesNotContainDuplicateTasks(rootOfClone));
//    }
//
//    @Test
//    void areEqualTrees() {
//        assertTrue(BehaviorTreeUtil.areEqualTrees(generateTree1(), generateTree1()));
//        assertFalse(BehaviorTreeUtil.areEqualTrees(generateTree1(), generateTree2()));
//
//        assertTrue(BehaviorTreeUtil.areEqualTrees(new IsApproachingTask(10), new IsApproachingTask(10)));
//        assertFalse(BehaviorTreeUtil.areEqualTrees(new IsApproachingTask(10), new IsApproachingTask(12)));
//    }
//
//    @RepeatedTest(1000)
//    void insertAndRemoveThoroughTest() {
//        Experiment1UnitInfo.init();
//        try {
//            Task root = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class, 3, 20);
//            Task randomCompositeTask = BehaviorTreeUtil.getRandomTask(root, true, BranchTask.class);
//
//            Task insertionRoot = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class, 3, 20);
//
//            Task rootWithInsertion = BehaviorTreeUtil.insertTask(root, randomCompositeTask, random.nextInt(randomCompositeTask.getChildCount()), insertionRoot);
//            Task rootWithRemovedInsertion = BehaviorTreeUtil.removeTask(rootWithInsertion, insertionRoot);
//
//            assertTrue(BehaviorTreeUtil.areEqualTrees(root, rootWithRemovedInsertion));
//
//
//            assertTrue(treeDoesNotContainDuplicateTasks(rootWithRemovedInsertion));
//
//        } catch (NoSuchTaskFoundException | InvalidArgumentException e) {
//            e.printStackTrace();
//        }
//    }
//
//
    private TempTask generateTree1() {
        TempTask waitTask = new TempWaitTask();
        TempTask moveToTargetTask1 = new TempMoveToTargetTask();
        TempTask moveToTargetTask2 = new TempMoveToTargetTask();
        TempTask isApproachingTask = new TempIsApproachingTask(20);
        TempTask isWithinTask = new TempIsWithinTask(42);
        TempTask turnToTargetTask = new TempTurnToTargetTask();

        TempSequence sequence = new TempSequence(moveToTargetTask1, isApproachingTask);
        TempSelector selector = new TempSelector(isWithinTask, waitTask);
        return new TempSequence(selector, sequence, moveToTargetTask2, turnToTargetTask);
    }

    private TempTask generateTree2() {
        TempTask waitTask = new TempWaitTask();
        TempTask moveToTargetTask = new TempMoveToTargetTask();
        TempTask isWithinTask = new TempIsWithinTask(60);
        TempTask turnToTargetTask = new TempTurnToTargetTask();

        TempSelector selector1= new TempSelector(moveToTargetTask);
        TempSelector selector2 = new TempSelector(isWithinTask, waitTask);
        return new TempSequence(selector1, selector2, turnToTargetTask);
    }
//
    private boolean treeDoesNotContainDuplicateTasks(TempTask root) {
        ArrayList<TempTask> taskList = BehaviorTreeUtil.getTasks(root, true, TempTask.class);
        HashSet<TempTask> taskSet = new HashSet<>(taskList);
        return taskList.size() == taskSet.size();
    }

    @Test
    void removeFollowingTasksOfAlwaysSuccessfulTasks() {
        TempTask root = new TempSelector(
                new TempSelector(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(20)
                ),
                new TempSelector(
                        new TempIsWithinTask(10), new TempIsApproachingTask(20), new TempMoveToTargetTask(), new TempWaitTask()
                ),
                new TempSequence(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(20)
                )
        );

//        GraphFrame graphFrame = Grapher.createNewFrame("asd");
//        GraphTab graphTab = new GraphTab("asd");

//        graphTab.add(root);

        BehaviorTreeUtil.removeFollowingTasksOfAlwaysSuccessfulTasks(root);
//        graphTab.add(root);
        assertEquals(11, BehaviorTreeUtil.getSize(root));

        BehaviorTreeUtil.clean(root);
//        graphTab.add(root);
        assertEquals(2, BehaviorTreeUtil.getSize(root));

//        graphFrame.addTab(graphTab);
//        graphFrame.display();
//        sleepSeconds(20);


        TempTask root2 = new TempSequence(
                new TempSelector(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(20)
                ),
                new TempSelector(
                        new TempIsWithinTask(10), new TempIsApproachingTask(20), new TempMoveToTargetTask(), new TempWaitTask()
                ),
                new TempSequence(
                        new TempMoveToTargetTask(), new TempMoveToTargetTask(), new TempIsWithinTask(20)
                )
        );

        BehaviorTreeUtil.removeFollowingTasksOfAlwaysSuccessfulTasks(root2);
        assertEquals(11, BehaviorTreeUtil.getSize(root2));

        BehaviorTreeUtil.clean(root2);
        assertEquals(10, BehaviorTreeUtil.getSize(root2));
    }
}