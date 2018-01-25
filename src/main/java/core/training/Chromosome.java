package core.training;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;

import java.util.ArrayList;

/**
 * Wrapper for behavior tree and fitness
 */
public class Chromosome {

    private Task btree;
    // TODO Change fitness to map?
    private ArrayList<Double> fitness;

    public Chromosome(Task btree) {
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
    public Chromosome clone(){
        Chromosome newEvalBtree = new Chromosome(BehaviorTreeUtil.clone(btree));
        newEvalBtree.setFitness(fitness);
        return newEvalBtree;
    }

    @Override
    public String toString() {
        return "Chromosome@" + hashCode() + " {Fitness: " + fitness + ", " + "Btree: " + btree + "}";
    }
}