package core.btree.operations;

import core.btree.tasks.blueprint.template.Task;
import core.btree.operations.mutations.*;
import core.unit.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import static core.util.SystemUtil.random;

public abstract class Mutator {

    private static List<Mutation> mutations = new ArrayList<>();
    static {
        mutations.add(new AddRandomSubtreeMutation(                         1, 1, true));
        mutations.add(new AddRandomSubtreeMutation(                         1, 0.995, false));
        mutations.add(new RemoveRandomSubtreeMutation(                      1, 1));
        mutations.add(new SwitchPositionsOfRandomSiblingTasksMutation(      1, 1));
        mutations.add(new ReplaceTreeWithSubtreeMutation(                   1, 0.99)); // TODO Too big change for a mutation?
        mutations.add(new ReplaceRandomTaskWithTaskOfSameTypeMutation(      1, 1));
        mutations.add(new RandomiseVariablesOfRandomVariableTaskMutation(   1, 1.02));
    }

    public static Task mutate(Task root, Class<? extends Unit> unitClass, double factorPower) {

        double totalWeight = 0;
        NavigableMap<Double, Mutation> selectionMap = new TreeMap<>();

        for (Mutation mutation : mutations) {
            if (mutation.canBePerformed(root)) {
                totalWeight += mutation.getWeight(factorPower);
                selectionMap.put(totalWeight, mutation);
            }
        }

        double randomValue = random.nextDouble() * totalWeight;
        Mutation selectedMutation = selectionMap.higherEntry(randomValue).getValue();

        Task newRoot = selectedMutation.mutate(root.cloneTask(), unitClass);
        return newRoot.getCleanVersion();
    }
}
