package core.training;

import core.model.btree.BehaviorTreeUtil;
import core.model.btree.EvaluatedBehaviorTree;
import core.unit.Unit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Population {

    Random random = new Random();

    private ArrayList<EvaluatedBehaviorTree> chromosomes;

    public Population() {
        chromosomes = new ArrayList<>();
    }

    public void generateRandomPopulation(int size, Class<? extends Unit> unitClass) {
        for (int i = 0; i < size; i++) {
            try {
                chromosomes.add(new EvaluatedBehaviorTree(BehaviorTreeUtil.generateRandomTree(unitClass)));
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(EvaluatedBehaviorTree btree) {
        chromosomes.add(btree);
    }

    public void sort(Comparator<EvaluatedBehaviorTree> comparator) {
        chromosomes.sort(comparator);
    }

    public int getSize() {
        return chromosomes.size();
    }

    public ArrayList<EvaluatedBehaviorTree> getChromosomes() {
        return chromosomes;
    }

    public EvaluatedBehaviorTree get(int i) {
        return chromosomes.get(i);
    }

    public EvaluatedBehaviorTree selectionTournament(int numOfContenders, Comparator<EvaluatedBehaviorTree> comparator) {
        ArrayList<EvaluatedBehaviorTree> listOfContenders = new ArrayList<>(numOfContenders);
        for (int i = 0; i < numOfContenders; i++) {
            listOfContenders.add(get(random.nextInt(getSize())));
        }
        return Collections.min(listOfContenders, comparator);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Population@" + hashCode() + " {");
        for (EvaluatedBehaviorTree evaluatedBehaviorTree : chromosomes) {
            sb.append("\n\t").append(evaluatedBehaviorTree);
        }
        sb.append("\n}");
        return sb.toString();
    }
}
