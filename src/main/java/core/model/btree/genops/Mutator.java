package core.model.btree.genops;

import core.BtreeAlt.TempTask;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.mutations.*;
import core.unit.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import static core.util.SystemUtil.random;

public abstract class Mutator {

    private static List<Mutation> mutations = new ArrayList<>();
    static {
        mutations.add(new AddRandomSubtreeMutation(1, true));
        mutations.add(new AddRandomSubtreeMutation(1, false));
//        mutations.add(new RemoveRandomSubtreeMutation(1));
        mutations.add(new SwitchPositionsOfRandomSiblingTasksMutation(1));
//        mutations.add(new ReplaceTreeWithSubtreeMutation(1)); // TODO Too big change for a mutation?
//        mutations.add(new ReplaceRandomTaskWithTaskOfSameTypeMutation(1));
        mutations.add(new RandomiseVariablesOfRandomVariableTaskMutation(1));
    }

    public static void mutate(TempTask root, Class<? extends Unit> unitClass) {

        double totalWeight = 0;
        NavigableMap<Double, Mutation> selectionMap = new TreeMap<>();

        for (Mutation mutation : mutations) {
            if (mutation.canBePerformed(root)) {
                totalWeight += mutation.getWeight();
                selectionMap.put(totalWeight, mutation);
            }
        }

        double randomValue = random.nextDouble() * totalWeight;
        Mutation selectedMutation = selectionMap.higherEntry(randomValue).getValue();

        selectedMutation.mutate(root, unitClass);
        BehaviorTreeUtil.clean(root);
    }
}
