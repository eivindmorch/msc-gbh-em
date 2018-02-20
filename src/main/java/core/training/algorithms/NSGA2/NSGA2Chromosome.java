package core.training.algorithms.NSGA2;

import core.BtreeAlt.TempTask;
import core.training.Chromosome;

import java.util.Comparator;
import java.util.HashSet;

public class NSGA2Chromosome extends Chromosome {

    int numOfDominators;
    HashSet<NSGA2Chromosome> dominates;
    int rank;
    double crowdingDistance;

    public NSGA2Chromosome(TempTask btree) {
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
        for (String key : getFitness().keySet()) {
            if (getFitness().get(key) > o.getFitness().get(key)) {
                return false;
            }
        }
        for (String key : getFitness().keySet()) {
            if (getFitness().get(key) < o.getFitness().get(key)) {
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
}
