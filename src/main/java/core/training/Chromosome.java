package core.training;

import core.btree.tasks.blueprint.template.Task;
import core.util.ToStringBuilder;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Wrapper for behavior tree and fitness
 */
public class Chromosome {

    // TODO Rename to behaviorTreeRoot
    private Task behaviourTreeRoot;
    private LinkedHashMap<String, Double> fitness;

    public Chromosome(Task behaviourTreeRoot) {
        this.behaviourTreeRoot = behaviourTreeRoot;
    }

    public Task getBehaviourTreeRoot() {
        return behaviourTreeRoot;
    }

    public LinkedHashMap<String, Double> getFitness() {
        return new LinkedHashMap<>(fitness);
    }

    public void setFitness(LinkedHashMap<String, Double> fitness) {
        this.fitness = new LinkedHashMap<>(fitness);
    }

//    @Override
//    public Chromosome clone(){
//        Chromosome newEvalBtree = new Chromosome(BehaviorTreeUtil.cloneTree(behaviourTreeRoot));
//        newEvalBtree.setFitness(fitness);
//        return newEvalBtree;
//    }

    @Override
    public String toString() {
        return ToStringBuilder.toStringBuilder(this)
                .add("fitness", (fitness == null) ? "null" : fitness)
                .add("behaviourTreeRoot", (behaviourTreeRoot == null) ? "null" : Integer.toHexString(behaviourTreeRoot.hashCode()))
                .toString();
    }

    public static Comparator<Chromosome> singleObjectiveComparator(String key) {
        return (o1, o2) -> {
            if (o1.getFitness().get(key) < o2.getFitness().get(key)) return -1;
            if (o1.getFitness().get(key) > o2.getFitness().get(key)) return 1;
            return 0;
        };
    }

    public boolean functionallyEquals(Chromosome o) {
        for (String key : this.fitness.keySet()) {
            if (!key.equals("Size") && !Objects.equals(this.fitness.get(key), o.fitness.get(key))) {
                return false;
            }
        }
        return true;
    }
}
