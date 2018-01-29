package core.model.btree.genops.mutations;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.unit.Unit;
import core.util.exceptions.NoSuchTasksFoundException;
import core.util.graphing.Grapher;

public class RemoveRandomSubtreeMutation extends Mutation {

    public RemoveRandomSubtreeMutation(double weight) {
        super(weight);
    }

    @Override
    public boolean canBePerformed(Task root) {
        return BehaviorTreeUtil.getSize(root) > 1;
    }

    @Override
    public Task mutate(Task root, Class<? extends Unit> unitClass) {
        Grapher.closeAllGraphs();
        Grapher.graph("Tree", root);
        try {
            Task randomRoot = BehaviorTreeUtil.getRandomTask(root, false, Task.class);
            Grapher.graph("Subtree", randomRoot);
            System.out.println(randomRoot);
            return BehaviorTreeUtil.removeTask(root, randomRoot);
        } catch (NoSuchTasksFoundException e) {
            e.printStackTrace();
            return BehaviorTreeUtil.clone(root);
        }
    }
}
