package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.federate.Federate;
import simulation.federate.PhysicalEntityUpdatedListener;
import simulation.federate.TickListener;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import unit.UnitHandler;
import unit.UnitLogger;

import java.util.concurrent.TimeUnit;

public class SimController implements TickListener, PhysicalEntityUpdatedListener {

    private final Logger logger = LoggerFactory.getLogger(SimController.class);


    public void init() {
        Federate federate = new Federate();
        federate.init();
        federate.addTickListener(this);
        federate.addPhysicalEntityUpdatedListener(this);
    }

    @Override
    public void tick(double timestamp) {
        // TODO Remove when list is resetting
        if (UnitHandler.getNumOfUnits() == 2 ) {
            UnitHandler.updateUnits(timestamp);
            UnitLogger.logAllRegisteredUnits();

//            Blackboard blackboard;
//            if (UnitHandler.get(0).getRole() == Role.FOLLOWER) {
//                blackboard = new Blackboard(units.get(0), units.get(1));
//            } else {
//                blackboard = new Blackboard(units.get(1), units.get(0));
//            }
//            if (btree == null) {
//                Move move = new Move();
//                btree = new GenBehaviorTree(move, blackboard);
//            }
//            btree.step();

            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void physicalEntityUpdated(PhysicalEntityObject physicalEntity) {
        UnitHandler.addUnit(physicalEntity);
    }

    public void initSimEngine() {
        SimEngine simEngine = new SimEngine();
        simEngine.init();
    }
}
