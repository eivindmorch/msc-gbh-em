package core.training.algorithms.NSGA2;

import com.badlogic.gdx.ai.btree.Task;
import core.training.Chromosome;

import java.util.Comparator;
import java.util.HashSet;

public class NSGA2Chromosome extends Chromosome {

    int numOfDominators;
    HashSet<NSGA2Chromosome> dominates;
    int rank;
    double crowdingDistance;

    public NSGA2Chromosome(Task btree) {
        super(btree);
    }

//    public boolean dominates(Chromosome o) {
//        if (getFitness().get(0) > o.getFitness().get(0)) return false;
//        if (getFitness().get(1) > o.getFitness().get(1)) return false;
//        if (getFitness().get(2) > o.getFitness().get(2)) return false;
//
//        if (getFitness().get(0) < o.getFitness().get(0)) return true;
//        if (getFitness().get(1) < o.getFitness().get(1)) return true;
//        if (getFitness().get(2) < o.getFitness().get(2)) return true;
//        return false;
//    }
    boolean dominates(Chromosome o) {
        for (int i = 0; i < getFitness().size(); i++) {
            if (getFitness().get(i) > o.getFitness().get(i)) {
                return false;
            }
        }
        for (int i = 0; i < getFitness().size(); i++) {
            if (getFitness().get(i) < o.getFitness().get(i)) {
                return true;
            }
        }
        return false;
    }

    static Comparator<NSGA2Chromosome> crowdingDistanceComparator() {
        return (o1, o2) -> {
            if (o1.crowdingDistance > o2.crowdingDistance) return -1;
            if (o1.crowdingDistance < o2.crowdingDistance) return 1;
            return 0;
        };
    }

    static Comparator<NSGA2Chromosome> nonDominationRankAndCrowdingDistanceComparator() {
        return (o1, o2) -> {
            if (o1.rank < o2.rank) return -1;
            if (o1.rank > o2.rank) return 1;
            if (o1.crowdingDistance > o2.crowdingDistance) return -1;
            if (o1.crowdingDistance < o2.crowdingDistance) return 1;
            return 0;
        };
    }

    static Comparator<NSGA2Chromosome> singleObjectiveComparator(int fitnessIndex) {
        return (o1, o2) -> {
            if (o1.getFitness().get(fitnessIndex) < o2.getFitness().get(fitnessIndex)) return -1;
            if (o1.getFitness().get(fitnessIndex) > o2.getFitness().get(fitnessIndex)) return 1;
            return 0;
        };
    }

//    static Comparator<NSGA2Chromosome> overallDeviationComparator() {
//        return (o1, o2) -> {
//            if (o1.fitness[0] < o2.fitness[0]) return -1;
//            if (o1.fitness[0] > o2.fitness[0]) return 1;
//            return 0;
//        };
//    }
//
//    static Comparator<NSGA2Chromosome> edgeValueComparator() {
//        return (o1, o2) -> {
//            if (o1.fitness[1] < o2.fitness[1]) return -1;
//            if (o1.fitness[1] > o2.fitness[1]) return 1;
//            return 0;
//        };
//    }
//
//    static Comparator<NSGA2Chromosome> connectivityComparator() {
//        return (o1, o2) -> {
//            if (o1.fitness[2] < o2.fitness[2]) return -1;
//            if (o1.fitness[2] > o2.fitness[2]) return 1;
//            return 0;
//        };
//    }
}
