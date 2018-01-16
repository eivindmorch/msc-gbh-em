package core.training;

import core.model.btree.EvaluatedGenBehaviorTree;
import core.model.btree.GenBehaviorTree;

import java.util.ArrayList;
import java.util.Comparator;

public class Population {

    private ArrayList<EvaluatedGenBehaviorTree> population;

    public Population() {
        population = new ArrayList<>();
    }

    public void generateRandomPopulation(int size) {
        for (int i = 0; i < size; i++) {
            population.add(new EvaluatedGenBehaviorTree(GenBehaviorTree.generateRandomTree()));
        }
    }

    public void add(GenBehaviorTree btree) {
        add(new EvaluatedGenBehaviorTree(btree));
    }

    public void add(EvaluatedGenBehaviorTree btree) {
        population.add(btree);
    }

    public EvaluatedGenBehaviorTree crossover(int index1, int index2) {
        // TODO
        return cloneElement(index1);
    }

    public EvaluatedGenBehaviorTree mutate(int index) {
        // TODO
        return cloneElement(index);
    }

    public EvaluatedGenBehaviorTree cloneElement(int index) {
        return population.get(index).clone();
    }

    public void sort(Comparator<EvaluatedGenBehaviorTree> comparator) {
        population.sort(comparator);
    }

    public int getSize() {
        return population.size();
    }

    public ArrayList<EvaluatedGenBehaviorTree> getPopulation() {
        return population;
    }

    public EvaluatedGenBehaviorTree get(int i) {
        return population.get(i);
    }


}
