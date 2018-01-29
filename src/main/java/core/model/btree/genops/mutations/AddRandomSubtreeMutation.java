package core.model.btree.genops.mutations;

import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTasksFoundException;

import java.lang.reflect.InvocationTargetException;

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
                Task randomTree = BehaviorTreeUtil.generateRandomTree(unitClass);
                return BehaviorTreeUtil.insertTask(root, randomRoot, random.nextInt(randomRoot.getChildCount() + 1), randomTree);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                return BehaviorTreeUtil.clone(root);
            }
        } catch (NoSuchTasksFoundException e) {
            e.printStackTrace();
            return BehaviorTreeUtil.clone(root);
        }
    }

}
