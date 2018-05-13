package core.btree.operations.mutations;

import core.BtreeAlt.CompositeTasks.TempCompositeTask;
import core.BtreeAlt.LeafTasks.TempLeafTask;
import core.BtreeAlt.TempTask;
import core.btree.operations.Mutation;
import core.unit.Unit;
import core.unit.UnitTypeInfo;
import core.util.exceptions.NoAvailableTaskClassException;
import core.util.exceptions.NoSuchTaskFoundException;

import java.util.ArrayList;

import static core.util.SystemUtil.random;


public class ReplaceRandomTaskWithTaskOfSameTypeMutation extends Mutation {

    public ReplaceRandomTaskWithTaskOfSameTypeMutation(double weight, double factor) {
        super(weight, factor);
    }

    @Override
    protected boolean canBePerformed(TempTask root) {
        return root.getSize() > 0;
    }

    @Override
    protected TempTask mutate(TempTask root, Class<? extends Unit> unitClass) {
        TempTask newRoot = root.cloneTask();

        try {
            TempTask taskToReplace = newRoot.getRandomTask(true, TempTask.class);
            UnitTypeInfo unitTypeInfo = UnitTypeInfo.getUnitInfoFromUnitClass(unitClass);

            ArrayList<Class<? extends TempTask>> availableTaskClasses = new ArrayList<>();

            if (taskToReplace instanceof TempLeafTask) {
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

            TempTask newTask = availableTaskClasses.get(random.nextInt(availableTaskClasses.size())).newInstance();

            if (taskToReplace instanceof TempCompositeTask) {
                ((TempCompositeTask) newTask).addChildren(taskToReplace.getChildren());
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
