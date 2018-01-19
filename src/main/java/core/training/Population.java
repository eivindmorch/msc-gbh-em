package core.training;

import core.model.btree.EvaluatedGenBehaviorTree;
import core.model.btree.GenBehaviorTree;
import core.unit.Unit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Population {

    Random random = new Random();

    private ArrayList<EvaluatedGenBehaviorTree> chromosomes;

    public Population() {
        chromosomes = new ArrayList<>();
    }

    public void generateRandomPopulation(int size, Class<? extends Unit> unitClass) {
        for (int i = 0; i < size; i++) {
            try {
                chromosomes.add(new EvaluatedGenBehaviorTree(GenBehaviorTree.generateRandomTree(unitClass)));
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(GenBehaviorTree btree) {
        add(new EvaluatedGenBehaviorTree(btree));
    }

    public void add(EvaluatedGenBehaviorTree btree) {
        chromosomes.add(btree);
    }

    public void sort(Comparator<EvaluatedGenBehaviorTree> comparator) {
        chromosomes.sort(comparator);
    }

    public int getSize() {
        return chromosomes.size();
    }

    public ArrayList<EvaluatedGenBehaviorTree> getChromosomes() {
        return chromosomes;
    }

    public EvaluatedGenBehaviorTree get(int i) {
        return chromosomes.get(i);
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
        for (EvaluatedGenBehaviorTree evaluatedGenBehaviorTree: chromosomes) {
            sb.append("\n\t").append(evaluatedGenBehaviorTree);
        }
        sb.append("\n}");
        return sb.toString();
    }
}
