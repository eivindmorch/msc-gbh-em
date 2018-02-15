//package core.model.btree.genops.mutations;
//
//import core.BtreeAlt.TempTask;
//import core.model.btree.BehaviorTreeUtil;
//import core.model.btree.genops.Mutation;
//import core.unit.Unit;
//import core.util.exceptions.NoSuchTaskFoundException;
//
//public class RemoveRandomSubtreeMutation extends Mutation {
//
//    public RemoveRandomSubtreeMutation(double weight) {
//        super(weight);
//    }
//
//    @Override
//    public boolean canBePerformed(TempTask root) {
//        try {
//            BehaviorTreeUtil.getRandomRemovableTask(root);
//        } catch (NoSuchTaskFoundException e) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void mutate(TempTask root, Class<? extends Unit> unitClass) {
//        try {
////            TempTask randomRoot = BehaviorTreeUtil.getRandomRemovableTask(root);
//            BehaviorTreeUtil.getRandomTask(root, false, )
//
//            return BehaviorTreeUtil.removeTask(root, randomRoot);
//        } catch (NoSuchTaskFoundException e) {
//            e.printStackTrace();
//            System.exit(1);
//            return null;
//        }
//    }
//}
