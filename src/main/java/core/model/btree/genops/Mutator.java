package core.model.btree.genops;

import com.badlogic.gdx.ai.btree.Task;
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
        mutations.add(new AddRandomSubtreeMutation(1));
        mutations.add(new RemoveRandomSubtreeMutation(1));
        mutations.add(new SwitchRandomSiblingsMutation(1));
        mutations.add(new ReplaceWithTaskOfSameTypeMutation(1));
        mutations.add(new RandomiseTaskVariablesMutation(1));
    }

    public static Task mutate(Task root, Class<? extends Unit> unitClass) {

        double totalWeight = 0;
        NavigableMap<Double, Mutation> selectionMap = new TreeMap<>();

        for (Mutation mutation : mutations) {
            if (mutation.canBePerformed(root)) {
                totalWeight += mutation.getWeight();
                selectionMap.put(totalWeight, mutation);
            }
        }
        System.out.println(selectionMap.size());

        double randomValue = random.nextDouble() * totalWeight;
        Mutation selectedMutation = selectionMap.higherEntry(randomValue).getValue();
        System.out.println(selectedMutation.getClass().getSimpleName());

        Task mutatedTree = selectedMutation.mutate(root, unitClass);
        return BehaviorTreeUtil.removeEmptyAndSingleChildCompositeTasks(mutatedTree);
    }
}
