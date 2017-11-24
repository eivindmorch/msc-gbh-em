package federate;

import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.exceptions.*;
import model.Unit;
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
import model.btree.Blackboard;
import model.btree.GenBehaviorTree;
import model.btree.task.Move;
import logging.UnitLogger;
import util.Values.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO Rename
public class Federate implements Runnable, HlaObjectListener, HlaObjectUpdateListener, TimeManagementListener {

    private final Logger logger = LoggerFactory.getLogger(Federate.class);

    private FederateManager federateManager;
    private volatile List<Unit> units = new ArrayList<>();

    private transient boolean running = true;
    private volatile boolean constrained = false;
    private volatile boolean regulated = false;

    private GenBehaviorTree btree;

    private final UnitLogger unitLogger = new UnitLogger();

    public Federate() {
        System.setProperty("hlalib-config-filepath", "src/main/resources/HlaLibConfig.xml");
    }

    public void initiate() {
        federateManager = HlaLib.init();
        federateManager.addTimeManagementListener(this);

        // Add listener for physical entities
        PhysicalEntityObject.addHlaObjectListener(this);

        federateManager.init();

        logger.info("Federate initiated");
    }

    @Override
    public void remoteObjectDiscovered(HlaObject object) {
        PhysicalEntityObject physicalEntity = (PhysicalEntityObject) object;
        physicalEntity.addObjectUpdateListener(this);
        physicalEntity.requestUpdateOnAllAttributes();
        if (units.size() >= 2) {
            reset();
        }
    }

    @Override
    public void hlaObjectUpdated(HlaObjectUpdatedEvent updatedEvent) {
        if (updatedEvent.getHlaObject() instanceof PhysicalEntityObject) {
            PhysicalEntityObject physicalEntity = (PhysicalEntityObject) updatedEvent.getHlaObject();

            String markingString = physicalEntity.getMarking().getMarking();

            try {
                Role role = Role.valueOf(markingString);
                ObjectInstanceHandle handle = physicalEntity.getObjectInstanceHandle();
                Unit unit = new Unit(handle, role);
                units.add(unit);
                logger.info("Unit " + markingString + " was added with handle " + handle + ".");
                unitLogger.register(unit);
            } catch (IllegalArgumentException e) {
            }
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
        if (units.size() == 2 ) {
            updateUnits(timestamp);

            unitLogger.logAllRegisteredUnits();

            Blackboard blackboard;
            if (units.get(0).getRole() == Role.FOLLOWER) {
                blackboard = new Blackboard(units.get(0), units.get(1));
            } else {
                blackboard = new Blackboard(units.get(1), units.get(0));
            }
            if (btree == null) {
                Move move = new Move();
                btree = new GenBehaviorTree(move, blackboard);
            }
            btree.step();

            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUnits(double timestamp) {
        for (Unit unit : units) {
            PhysicalEntityObject physicalEntity = PhysicalEntityObject.getAllPhysicalEntitys().get(unit.getHandle());
            unit.setRawData(timestamp, physicalEntity);
        }
        // TODO Remove when list is resetting
        if (units.size() == 2) {
            Unit unit0 = units.get(0);
            Unit unit1 = units.get(1);
            unit0.updateProcessedData(timestamp, unit1);
            unit1.updateProcessedData(timestamp, unit0);
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
        units = new ArrayList<>();
        unitLogger.reset();
        // TODO
        // Reset federation timestamp to 0
        // Reset scenario
    }

    public static void main(String[] args) {
        Federate federate = new Federate();
        federate.initiate();
    }

}


