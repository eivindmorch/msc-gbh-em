package training;

import model.btree.GenBehaviorTree;

import java.util.ArrayList;

public class Population {

    ArrayList<GenBehaviorTree> population;

    public void crossover(GenBehaviorTree btree1, GenBehaviorTree btree2) {}

    public void mutate(GenBehaviorTree btree) {}

    void evaluatePopulation(Example example) {

    }

    void selectNextPopulation() {
        // Elitism
        // Tournament crossover
    }

}
