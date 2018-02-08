package core.model.btree.genops.mutations;

import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import com.sun.javaws.exceptions.InvalidArgumentException;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTaskFoundException;

import static core.util.SystemUtil.random;

public class AddRandomSubtreeMutation extends Mutation {

    public AddRandomSubtreeMutation(double weight) {
        super(weight);
    }

    @Override
    public boolean canBePerformed(Task root) {
        return !BehaviorTreeUtil.getTasks(root, true, BranchTask.class).isEmpty();
    }

    @Override
    public Task mutate(Task root, Class<? extends Unit> unitClass) {
        try {
            Task randomRoot = BehaviorTreeUtil.getRandomTask(root, true, BranchTask.class);
            try {
                Task randomTree = BehaviorTreeUtil.generateRandomTree(unitClass, 3, 5);
                return BehaviorTreeUtil.insertTask(root, randomRoot, random.nextInt(randomRoot.getChildCount() + 1), randomTree);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
                return BehaviorTreeUtil.cloneTree(root);
            }
        } catch (NoSuchTaskFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

}
