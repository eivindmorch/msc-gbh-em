//package core.model.btree.genops.mutations;
//
//import core.BtreeAlt.TempTask;
//import core.model.btree.BehaviorTreeUtil;
//import core.model.btree.genops.Mutation;
//import core.unit.Unit;
//import core.util.exceptions.NoAvailableTaskClassException;
//import core.util.exceptions.NoSuchTaskFoundException;
//
//
//public class ReplaceRandomTaskWithTaskOfSameTypeMutation extends Mutation {
//
//    public ReplaceRandomTaskWithTaskOfSameTypeMutation(double weight) {
//        super(weight);
//    }
//
//    @Override
//    public boolean canBePerformed(TempTask root) {
//        return BehaviorTreeUtil.getSize(root) > 0;
//    }
//
//    @Override
//    public void mutate(TempTask root, Class<? extends Unit> unitClass) {
//        try {
//            TempTask randomTask = BehaviorTreeUtil.getRandomTask(root, true, TempTask.class);
//            return BehaviorTreeUtil.randomiseIndividualTask(root, randomTask, unitClass);
//        } catch (NoSuchTaskFoundException | NoAvailableTaskClassException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
//    }
//}
