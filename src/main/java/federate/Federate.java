package federate;

import hla.rti1516e.exceptions.*;
import model.btree.Blackboard;
import unit.UnitHandler;
import no.ffi.hlalib.HlaLib;
import no.ffi.hlalib.HlaObject;
import no.ffi.hlalib.events.HlaObjectRemovedEvent;
import no.ffi.hlalib.events.HlaObjectUpdatedEvent;
import no.ffi.hlalib.listeners.HlaObjectListener;
import no.ffi.hlalib.listeners.HlaObjectUpdateListener;
import no.ffi.hlalib.listeners.TimeManagementListener;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import no.ffi.hlalib.services.FederateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unit.UnitLogger;

import java.util.concurrent.TimeUnit;

// TODO Rename
public class Federate implements Runnable, HlaObjectListener, HlaObjectUpdateListener, TimeManagementListener {

    private final Logger logger = LoggerFactory.getLogger(Federate.class);

    private FederateManager federateManager;

    private transient boolean running = true;
    private volatile boolean constrained = false;
    private volatile boolean regulated = false;

    public Federate() {
        System.setProperty("hlalib-config-filepath", "src/main/resources/HlaLibConfig.xml");
    }

    public void initiate() {
        federateManager = HlaLib.init();
        federateManager.addTimeManagementListener(this);

        // Add listener for physical entities
        PhysicalEntityObject.addHlaObjectListener(this);

        federateManager.init();

        logger.info("Federate initiated.");
    }

    @Override
    public void remoteObjectDiscovered(HlaObject object) {
        PhysicalEntityObject physicalEntity = (PhysicalEntityObject) object;
        physicalEntity.addObjectUpdateListener(this);
        physicalEntity.requestUpdateOnAllAttributes();
        if (UnitHandler.getNumOfUnits() >= 2) {
            reset();
        }
    }

    @Override
    public void hlaObjectUpdated(HlaObjectUpdatedEvent updatedEvent) {
        if (updatedEvent.getHlaObject() instanceof PhysicalEntityObject) {
            PhysicalEntityObject physicalEntity = (PhysicalEntityObject) updatedEvent.getHlaObject();
            UnitHandler.addUnit(physicalEntity);
            physicalEntity.removeObjectUpdateListener(this);
        }
    }

    @Override
    public void remoteObjectRemoved(HlaObjectRemovedEvent removedEvent) {}

    @Override
    public void localObjectCreated(HlaObject object) {}

    @Override
    public void localObjectRemoved(HlaObject hlaObject) {}

    @Override
    public void run() {
        while (running) {
            try {
                federateManager.requestTimeAdvanceAndBlock(federateManager.getTimestamp());
            } catch (InterruptedException | RTIexception saveInProgress) {
                saveInProgress.printStackTrace();
            }
            tick(federateManager.getLogicalTime());
        }
    }

    private void tick(double timestamp) {
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
    public void timeRegulationEnabled(Double logicalTime) {
        regulated = true;
        checkConditions();
    }

    @Override
    public void timeConstrainedEnabled(Double logicalTime) {
        constrained = true;
        checkConditions();
    }

    private void checkConditions() {
        if (regulated && constrained) {
            new Thread(this).start(); // Wait until simEngine has initiated time-regulation
        }
    }

    public void reset() {
        UnitHandler.reset();
        UnitLogger.reset();
        // TODO
        // Reset federation timestamp to 0
        // Reset scenario
    }

    public static void main(String[] args) {
        Federate federate = new Federate();
        federate.initiate();
    }

}


