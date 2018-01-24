package core.model.btree;

import com.badlogic.gdx.ai.btree.Task;

import java.util.ArrayList;

/**
 * Wrapper for behavior tree and fitness
 */
public class EvaluatedBehaviorTree {

    private Task btree;
    // TODO Change fitness to map?
    private ArrayList<Double> fitness;

    public EvaluatedBehaviorTree(Task btree) {
        this.btree = btree;
    }

    public Task getBtree() {
        return btree;
    }

    public ArrayList<Double> getFitness() {
        return fitness;
    }

    public void setFitness(ArrayList<Double> fitness) {
        this.fitness = new ArrayList<>(fitness);
    }

    @Override
    public EvaluatedBehaviorTree clone(){
        EvaluatedBehaviorTree newEvalBtree = new EvaluatedBehaviorTree(BehaviorTreeUtil.clone(btree));
        newEvalBtree.setFitness(fitness);
        return newEvalBtree;
    }

    @Override
    public String toString() {
        return "EvaluatedBehaviorTree@" + hashCode() + " {Fitness: " + fitness + ", " + "Btree: " + btree + "}";
    }
}
