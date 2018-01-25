package core.training;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.unit.Unit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Population<C extends Chromosome> {

    Random random = new Random();

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
                Task randomTree = BehaviorTreeUtil.generateRandomTree(unitClass);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Population@" + hashCode() + " {");
        for (C chromosome : chromosomes) {
            sb.append("\n\t").append(chromosome);
        }
        sb.append("\n}");
        return sb.toString();
    }
}
