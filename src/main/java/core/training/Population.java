package core.training;

import core.model.btree.EvaluatedGenBehaviorTree;
import core.model.btree.GenBehaviorTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Population {

    Random random = new Random();

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

    public EvaluatedGenBehaviorTree crossover(int parent1Index, int parent2Index) {
        return crossover(get(parent1Index), get(parent2Index));
    }

    public EvaluatedGenBehaviorTree crossover(EvaluatedGenBehaviorTree parent1, EvaluatedGenBehaviorTree parent2) {
        // TODO
        return parent1;
    }

    public EvaluatedGenBehaviorTree mutate(int btreeIndex) {
        return mutate(get(btreeIndex));
    }

    public EvaluatedGenBehaviorTree mutate(EvaluatedGenBehaviorTree btree) {
        // TODO
        return btree.clone();
    }

    public EvaluatedGenBehaviorTree cloneElement(int index) {
        return get(index).clone();
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

    public EvaluatedGenBehaviorTree selectionTournament(int numOfContenders, Comparator<EvaluatedGenBehaviorTree> comparator) {
        ArrayList<EvaluatedGenBehaviorTree> listOfContenders = new ArrayList<>(numOfContenders);
        for (int i = 0; i < numOfContenders; i++) {
            listOfContenders.add(get(random.nextInt(getSize())));
        }
        return Collections.min(listOfContenders, comparator);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Population@" + hashCode() + " {");
        for (EvaluatedGenBehaviorTree evaluatedGenBehaviorTree: population) {
            sb.append("\n\t").append(evaluatedGenBehaviorTree);
        }
        sb.append("\n}");
        return sb.toString();
    }
}
