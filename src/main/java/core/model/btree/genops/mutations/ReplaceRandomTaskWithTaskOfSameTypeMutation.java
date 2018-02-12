package core.model.btree.genops.mutations;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoAvailableTaskClassException;
import core.util.exceptions.NoSuchTaskFoundException;


public class ReplaceRandomTaskWithTaskOfSameTypeMutation extends Mutation {

    public ReplaceRandomTaskWithTaskOfSameTypeMutation(double weight) {
        super(weight);
    }

    @Override
    public boolean canBePerformed(Task root) {
        return BehaviorTreeUtil.getSize(root) > 0;
    }

    @Override
    public Task mutate(Task root, Class<? extends Unit> unitClass) {
        try {
            Task randomTask = BehaviorTreeUtil.getRandomTask(root, true, Task.class);
            return BehaviorTreeUtil.randomiseIndividualTask(root, randomTask, unitClass);
        } catch (NoSuchTaskFoundException | NoAvailableTaskClassException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
