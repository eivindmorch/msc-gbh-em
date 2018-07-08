package core.btree.operations.mutations;

import core.btree.tasks.blueprint.template.Task;
import core.btree.tasks.blueprint.template.composite.CompositeTask;
import core.btree.tasks.blueprint.template.leaf.LeafTask;
import core.btree.operations.Mutation;
import core.unit.Unit;
import core.unit.UnitHandler;
import core.unit.UnitTypeInfo;
import core.util.exceptions.NoAvailableTaskClassException;
import core.util.exceptions.NoSuchTaskFoundException;

import java.util.ArrayList;

import static core.util.SystemUtil.random;


public class ReplaceRandomTaskWithTaskOfSameTypeMutation extends Mutation {

    public ReplaceRandomTaskWithTaskOfSameTypeMutation(double weight, double factorBase) {
        super(weight, factorBase);
    }

    @Override
    protected boolean canBePerformed(Task root) {
        return root.getSize() > 0;
    }

    @Override
    protected Task mutate(Task root, Class<? extends Unit> unitClass) {
        Task newRoot = root.cloneTask();

        try {
            Task taskToReplace = newRoot.getRandomTask(true, Task.class);
            UnitTypeInfo unitTypeInfo = UnitHandler.getUnitTypeInfoFromUnitClass(unitClass);

            ArrayList<Class<? extends Task>> availableTaskClasses = new ArrayList<>();

            if (taskToReplace instanceof LeafTask) {
                availableTaskClasses.addAll(unitTypeInfo.getAvailableLeafTaskClasses());
            } else {
                availableTaskClasses.addAll(unitTypeInfo.getAvailableCompositeTaskClasses());
            }

            availableTaskClasses.remove(taskToReplace.getClass());

            if (availableTaskClasses.isEmpty()) {
                throw new NoAvailableTaskClassException(
                        "No available task classes for " + taskToReplace.getDisplayName()
                                + "(" + taskToReplace.getClass().getTypeName() + " type)"
                );
            }

            Task newTask = availableTaskClasses.get(random.nextInt(availableTaskClasses.size())).newInstance();

            if (taskToReplace instanceof CompositeTask) {
                ((CompositeTask) newTask).addChildren(taskToReplace.getChildren());
            }

            if (taskToReplace == newRoot) {
                return taskToReplace;
            } else {
                taskToReplace.getParent().replaceChild(taskToReplace, newTask);
                return newRoot;
            }

        } catch (NoSuchTaskFoundException | NoAvailableTaskClassException |InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
