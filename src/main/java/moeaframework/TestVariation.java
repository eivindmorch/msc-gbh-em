package moeaframework;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import experiments.experiment1.unit.FollowerUnit;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import static core.util.SystemUtil.random;

public class TestVariation implements Variation {

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public Solution[] evolve(Solution[] solutions) {
        Task parent1TreeRoot = ((TestSolution)solutions[0]).getBtreeRoot();
        Task parent2TreeRoot = ((TestSolution)solutions[1]).getBtreeRoot();

        Solution[] result = new Solution[1];
        Task behaviorTree;
        if (random.nextBoolean()) {
            behaviorTree = BehaviorTreeUtil.crossover(parent1TreeRoot, parent2TreeRoot);
        } else {
            behaviorTree = BehaviorTreeUtil.mutate(parent1TreeRoot, FollowerUnit.class);
        }
        result[0] = new TestSolution(0, solutions[0].getNumberOfObjectives(), behaviorTree);
        return result;
    }
}
