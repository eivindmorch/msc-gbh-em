package moeaframework;

import core.model.btree.BehaviorTreeUtil;
import experiments.experiment1.unit.FollowerUnit;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Solution;

import java.lang.reflect.InvocationTargetException;

public class TestInitialization implements Initialization {

    private int numOfSolutions;
    private int numOfObjectives;

    public TestInitialization(int numOfSolutions, int numOfObjectives) {
        this.numOfSolutions = numOfSolutions;
        this.numOfObjectives = numOfObjectives;
    }

    @Override
    public Solution[] initialize() {
        Solution[] solutions = new Solution[numOfSolutions];

        for (int i = 0; i < numOfSolutions; i++) {
            try {
                solutions[i] = new TestSolution(0, numOfObjectives, BehaviorTreeUtil.generateRandomTree(FollowerUnit.class));
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return solutions;
    }
}
