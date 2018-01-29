package core.model.btree.genops.mutations;

import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTasksFoundException;

import java.util.ArrayList;

import static core.util.SystemUtil.random;

public class SwitchRandomSiblingsMutation extends Mutation {

    public SwitchRandomSiblingsMutation(double weight) {
        super(weight);
    }

    @Override
    public boolean canBePerformed(Task root) {
        try {
            BehaviorTreeUtil.getRandomTask(root, true, BranchTask.class, 2);
        } catch (NoSuchTasksFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public Task mutate(Task root, Class<? extends Unit> unitClass) {
        try {
            Task randomRoot = BehaviorTreeUtil.getRandomTask(root, true, BranchTask.class, 2);
            int numberOfChildren = randomRoot.getChildCount();
            ArrayList<Task> childList = new ArrayList<>(numberOfChildren);
            for (int i = 0; i < numberOfChildren; i++) {
                childList.add(randomRoot.getChild(i));
            }

            Task child1 = childList.remove(random.nextInt(childList.size()));
            Task child2 = childList.remove(random.nextInt(childList.size()));

            return BehaviorTreeUtil.switchTasks(root, child1, child2);

        } catch (NoSuchTasksFoundException e) {
            e.printStackTrace();
            return BehaviorTreeUtil.clone(root);
        }
    }
}
