package core.model.btree.genops.mutations;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTasksFoundException;

public class RemoveRandomSubtreeMutation extends Mutation {

    public RemoveRandomSubtreeMutation(double weight) {
        super(weight);
    }

    @Override
    public boolean canBePerformed(Task root) {
        try {
            BehaviorTreeUtil. getRandomRemovableTask(root);
        } catch (NoSuchTasksFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public Task mutate(Task root, Class<? extends Unit> unitClass) {
        try {
            Task randomRoot = BehaviorTreeUtil.getRandomRemovableTask(root);
            return BehaviorTreeUtil.removeTask(root, randomRoot);
        } catch (NoSuchTasksFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
