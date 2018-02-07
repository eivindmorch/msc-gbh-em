package core.training;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.unit.Unit;
import core.util.ToStringBuilder;

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

    public Population(Population<C> population) {
        chromosomes = new ArrayList<>(population.getChromosomes());
    }

    public static <C extends Chromosome> Population<C> generateRandomPopulation(
            int size, Class<? extends Unit> unitClass, Class<C> chromosomeClass)
    {
        Population<C> population = new Population<>();
        while (population.getSize() < size) {
            try {
                Constructor<C> chromosomeConstructor = chromosomeClass.getConstructor(Task.class);
                Task randomTree = BehaviorTreeUtil.generateRandomTree(unitClass);
                if (!population.containsChromosomeWithEqualTree(randomTree)) {
                    C chromosome = chromosomeConstructor.newInstance(randomTree);
                    population.add(chromosome);
                }
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

    public void removeAll(ArrayList<C> chromosomes) {
        this.chromosomes.removeAll(chromosomes);
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

    public boolean containsChromosomeWithEqualTree(Task root) {
        for (Chromosome chromosome : chromosomes) {
            if (BehaviorTreeUtil.areEqualTrees(chromosome.getBtree(), root)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toStringBuilder(this)
                .add("size", getSize())
                .addListedElements(chromosomes)
                .toString();
    }
}
