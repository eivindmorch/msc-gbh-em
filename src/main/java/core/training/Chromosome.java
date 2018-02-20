package core.training;

import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.TempTask;
import core.model.btree.BehaviorTreeUtil;
import core.util.ToStringBuilder;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Wrapper for behavior tree and fitness
 */
public class Chromosome {

    // TODO Rename to behaviorTreeRoot
    private TempTask btree;
    private HashMap<String, Double> fitness;

    public Chromosome(TempTask btree) {
        this.btree = btree;
    }

    public TempTask getBtree() {
        return btree;
    }

    public HashMap<String, Double> getFitness() {
        return new HashMap<>(fitness);
    }

    public void setFitness(HashMap<String, Double> fitness) {
        this.fitness = new HashMap<>(fitness);
    }

//    @Override
//    public Chromosome clone(){
//        Chromosome newEvalBtree = new Chromosome(BehaviorTreeUtil.cloneTree(btree));
//        newEvalBtree.setFitness(fitness);
//        return newEvalBtree;
//    }

    @Override
    public String toString() {
        return ToStringBuilder.toStringBuilder(this)
                .add("fitness", (fitness == null) ? "null" : fitness)
                .add("btree", (btree == null) ? "null" : Integer.toHexString(btree.hashCode()))
                .toString();
    }

    public static Comparator<Chromosome> singleObjectiveComparator(String key) {
        return (o1, o2) -> {
            if (o1.getFitness().get(key) < o2.getFitness().get(key)) return -1;
            if (o1.getFitness().get(key) > o2.getFitness().get(key)) return 1;
            return 0;
        };
    }
}
