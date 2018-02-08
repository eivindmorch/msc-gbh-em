package core.model.btree.genops.mutations;

import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

public class ReplaceTreeWithSubtreeMutation extends Mutation{

    public ReplaceTreeWithSubtreeMutation(double weight) {
        super(weight);
    }

    @Override
    public boolean canBePerformed(Task root) {
        return !BehaviorTreeUtil.getTasks(root, false, BranchTask.class).isEmpty();
    }

    @Override
    public Task mutate(Task root, Class<? extends Unit> unitClass) {
        try {
            Task randomRoot = BehaviorTreeUtil.getRandomTask(root, false, BranchTask.class);
            return BehaviorTreeUtil.cloneTree(randomRoot);
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
