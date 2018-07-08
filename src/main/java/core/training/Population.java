package core.training;

import com.sun.javaws.exceptions.InvalidArgumentException;
import core.btree.tasks.blueprint.template.Task;
import core.btree.BehaviorTreeUtil;
import core.unit.Unit;
import core.util.ToStringBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static core.util.SystemUtil.random;

public class Population<C extends Chromosome> {

    private ArrayList<C> chromosomes;

    public Population() {
        chromosomes = new ArrayList<>();
    }

    public Population(Population<C> population) {
        chromosomes = new ArrayList<>(population.getChromosomes());
    }


    /**
     *
     * @param numberOfChromosomes number of chromosomes the population should contain
     * @param unitClass the class of the unit the behavior trees are intended for
     * @param chromosomeClass the class to be used for chromosomes
     * @param maximumTreeSize the maximum allowed size for the behavior trees
     * @param <C> the type of the {@code chromosomeClass}, extending {@link Chromosome}
     * @return a population containing {@code numberOfChromosomes} chromosomes of type {@code C}
     */
    public static <C extends Chromosome> Population<C> generateRandomPopulation(
            Class<? extends Unit> unitClass,
            Class<C> chromosomeClass,
            int numberOfChromosomes,
            int minimumTreeSize,
            int maximumTreeSize
    ) throws InvalidArgumentException, TimeoutException {
        Population<C> population = new Population<>();

        while (population.getSize() < numberOfChromosomes) {
            try {
                Constructor<C> chromosomeConstructor = chromosomeClass.getConstructor(Task.class);

                int numberOfAttempts = 0;
                Task randomTree;
                do {
                    if (numberOfAttempts == 1000) {
                        throw new TimeoutException(
                                "Could not successfully create "
                                        + numberOfChromosomes
                                        + " unique trees with size between "
                                        + minimumTreeSize
                                        + " and "
                                        + maximumTreeSize);
                    }
                    numberOfAttempts++;
                    randomTree = BehaviorTreeUtil.generateRandomTree(unitClass, minimumTreeSize, maximumTreeSize);
                } while (population.containsChromosomeWithEqualTree(randomTree));

                C chromosome = chromosomeConstructor.newInstance(randomTree);
                population.add(chromosome);

            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return population;
    }

    public static <C extends Chromosome> Population<C> generateTestPopulation(Class<C> chromosomeClass, int numberOfChromosomes) {
        Population<C> population = new Population<>();

        while (population.getSize() < numberOfChromosomes) {
            try {
                Constructor<C> chromosomeConstructor = chromosomeClass.getConstructor(Task.class);

                Task testTree = BehaviorTreeUtil.generateTestTree();
                C chromosome = chromosomeConstructor.newInstance(testTree);
                population.add(chromosome);

            } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
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

    public void remove(C chromosome) {
        chromosomes.remove(chromosome);
    }

    public void removeAll(ArrayList<C> chromosomes) {
        this.chromosomes.removeAll(chromosomes);
    }

    public void sort(Comparator<? super C> comparator) {
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
            if (chromosome.getBehaviourTreeRoot().structurallyEquals(root)) {
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
