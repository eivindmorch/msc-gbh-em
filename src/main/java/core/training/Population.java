package core.training;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.training.algorithms.NSGA2.NSGA2Chromosome;
import core.unit.Unit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static core.util.SystemUtil.random;

public class Population<C extends Chromosome> {

    private ArrayList<C> chromosomes;

    public Population() {
        chromosomes = new ArrayList<>();
    }

    public static <C extends Chromosome> Population<C> generateRandomPopulation(
            int size, Class<? extends Unit> unitClass, Class<C> chromosomeClass)
    {
        Population<C> population = new Population<>();
        for (int i = 0; i < size; i++) {
            try {
                Constructor<C> chromosomeConstructor = chromosomeClass.getConstructor(Task.class);
//                Task randomTree = BehaviorTreeUtil.generateRandomTree(unitClass);
                Task randomTree = BehaviorTreeUtil.generateTestTree();
                C chromosome = chromosomeConstructor.newInstance(randomTree);
                population.add(chromosome);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return population;
    }

    public void add(C btree) {
        chromosomes.add(btree);
    }

    public void addAll(List<C> btreeList) {
        chromosomes.addAll(btreeList);
    }

    public void addAll(Population<C> population) {
        addAll(population.getChromosomes());
    }

    public C remove(int i) {
        return chromosomes.remove(i);
    }

    public void sort(Comparator<C> comparator) {
        chromosomes.sort(comparator);
    }

    public int getSize() {
        return chromosomes.size();
    }

    public ArrayList<C> getChromosomes() {
        return chromosomes;
    }

    public C get(int i) {
        return chromosomes.get(i);
    }

    public C selectionTournament(int numOfContenders, Comparator<C> comparator) {
        ArrayList<C> listOfContenders = new ArrayList<>(numOfContenders);
        for (int i = 0; i < numOfContenders; i++) {
            listOfContenders.add(get(random.nextInt(getSize())));
        }
        return Collections.min(listOfContenders, comparator);
    }

    public Population<C> shallowCopy() {
        Population<C> copy = new Population<>();
        copy.addAll(chromosomes);
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Population@" + hashCode() + " {");
        for (C chromosome : chromosomes) {
            sb.append("\n\t").append(chromosome);
        }
        sb.append("\n}");
        return sb.toString();
    }

    public void removeAll(ArrayList<C> chromosomes) {
        this.chromosomes.removeAll(chromosomes);
    }
}
