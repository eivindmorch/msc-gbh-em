package core.model.btree;

import java.util.ArrayList;

public class EvaluatedGenBehaviorTree {

    private GenBehaviorTree btree;
    // TODO Change fitness to map?
    private ArrayList<Double> fitness;

    public EvaluatedGenBehaviorTree(GenBehaviorTree btree) {
        this.btree = btree;
    }

    public GenBehaviorTree getBtree() {
        return btree;
    }

    public void setBtree(GenBehaviorTree btree) {
        this.btree = btree;
    }

    public ArrayList<Double> getFitness() {
        return fitness;
    }

    public void setFitness(ArrayList<Double> fitness) {
        this.fitness = new ArrayList<>(fitness);
    }

    @Override
    public EvaluatedGenBehaviorTree clone(){
        EvaluatedGenBehaviorTree newEvalBtree = new EvaluatedGenBehaviorTree(btree.clone());
        newEvalBtree.setFitness(fitness);
        return newEvalBtree;
    }

    @Override
    public String toString() {
        return "EvaluatedGenBehaviorTree@" + hashCode() + " {Fitness: " + fitness + ", " + "Btree: " + btree + "}";
    }
}
