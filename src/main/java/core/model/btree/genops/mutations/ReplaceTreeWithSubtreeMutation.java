//package core.model.btree.genops.mutations;
//
//import core.BtreeAlt.CompositeTasks.TempCompositeTask;
//import core.BtreeAlt.TempTask;
//import core.model.btree.BehaviorTreeUtil;
//import core.model.btree.genops.Mutation;
//import core.unit.Unit;
//import core.util.exceptions.NoSuchTaskFoundException;
//
//public class ReplaceTreeWithSubtreeMutation extends Mutation{
//
//    public ReplaceTreeWithSubtreeMutation(double weight) {
//        super(weight);
//    }
//
//    @Override
//    public boolean canBePerformed(TempTask root) {
//        try {
//            BehaviorTreeUtil.getRandomTask(root, false, TempCompositeTask.class, 2);
//            return true;
//        } catch (NoSuchTaskFoundException e) {
//            return false;
//        }
//    }
//
//    @Override
//    public void mutate(TempTask root, Class<? extends Unit> unitClass) {
//        try {
//            TempTask randomRoot = BehaviorTreeUtil.getRandomTask(root, false, TempCompositeTask.class, 2);
//            return BehaviorTreeUtil.cloneTree(randomRoot);
//        } catch (NoSuchTaskFoundException e) {
//            e.printStackTrace();
//            System.exit(1);
//            return null;
//        }
//    }
//}
