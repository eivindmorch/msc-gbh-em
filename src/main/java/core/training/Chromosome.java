package core.training;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.util.ToStringBuilder;

import java.util.ArrayList;

/**
 * Wrapper for behavior tree and fitness
 */
public class Chromosome {

    // TODO Rename to behaviorTreeRoot
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
        Chromosome newEvalBtree = new Chromosome(BehaviorTreeUtil.cloneTree(btree));
        newEvalBtree.setFitness(fitness);
        return newEvalBtree;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toStringBuilder(this)
                .add("fitness", (fitness == null) ? "null" : fitness)
                .add("btree", (btree == null) ? "null" : Integer.toHexString(btree.hashCode()))
                .toString();
    }
}
