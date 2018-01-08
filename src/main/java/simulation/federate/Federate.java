package simulation.federate;

import hla.rti1516e.exceptions.RTIexception;
import no.ffi.hlalib.HlaLib;
import no.ffi.hlalib.HlaObject;
import no.ffi.hlalib.datatypes.fixedRecordData.EntityIdentifierStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.FederateIdentifierStruct;
import no.ffi.hlalib.datatypes.variantRecordData.CgfCommand;
import no.ffi.hlalib.events.HlaObjectRemovedEvent;
import no.ffi.hlalib.events.HlaObjectUpdatedEvent;
import no.ffi.hlalib.interactions.HLAinteractionRoot.CgfControlInteraction;
import no.ffi.hlalib.listeners.HlaObjectListener;
import no.ffi.hlalib.listeners.HlaObjectUpdateListener;
import no.ffi.hlalib.listeners.TimeManagementListener;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import no.ffi.hlalib.services.FederateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class Federate implements Runnable, HlaObjectListener, HlaObjectUpdateListener, TimeManagementListener {

    private static Federate instance;

    private final Logger logger = LoggerFactory.getLogger(Federate.class);

    private FederateManager federateManager;

    private transient boolean running = true;
    private volatile boolean constrained = false;
    private volatile boolean regulated = false;

    private ArrayList<TickListener> tickListeners;
    private ArrayList<PhysicalEntityUpdatedListener> physicalEntityUpdatedListeners;

    private Federate() {
        System.setProperty("hlalib-config-filepath", "src/main/resources/HlaLibConfig.xml");
    }

    public static Federate getInstance() {
        if (instance == null) {
            instance = new Federate();
        }
        return instance;
    }

    public void init() {
        tickListeners = new ArrayList<>();
        physicalEntityUpdatedListeners = new ArrayList<>();

        federateManager = HlaLib.init();
        federateManager.addTimeManagementListener(this);

        // Add listener for physical entities
        PhysicalEntityObject.addHlaObjectListener(this);

        federateManager.init();

        logger.info("Federate initiated.");
    }

    public void addTickListener(TickListener tickListener) {
        tickListeners.add(tickListener);
    }

    public void addPhysicalEntityUpdatedListener(PhysicalEntityUpdatedListener physicalEntityUpdatedListener) {
        physicalEntityUpdatedListeners.add(physicalEntityUpdatedListener);
    }

    @Override
    public void remoteObjectDiscovered(HlaObject object) {
        if (object instanceof PhysicalEntityObject) {
            PhysicalEntityObject physicalEntity = (PhysicalEntityObject) object;
            physicalEntity.addObjectUpdateListener(this);
            physicalEntity.requestUpdateOnAllAttributes();
        }
    }

    @Override
    public void hlaObjectUpdated(HlaObjectUpdatedEvent updatedEvent) {
        if (updatedEvent.getHlaObject() instanceof PhysicalEntityObject) {
            PhysicalEntityObject physicalEntity = (PhysicalEntityObject) updatedEvent.getHlaObject();
            physicalEntityUpdatedListeners.forEach(
                    physicalEntityUpdatedListener -> physicalEntityUpdatedListener.physicalEntityUpdated(physicalEntity)
            );
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
                // TODO Wait until SimEngine is connected to RTI federation
                // TODO Synchronisation points (scenario loaded)
                federateManager.requestTimeAdvanceAndBlock(federateManager.getTimestamp());
            } catch (InterruptedException | RTIexception saveInProgress) {
                saveInProgress.printStackTrace();
            }
            // TODO Possible to use simulated exercise time?
            tick(federateManager.getLogicalTime());
        }
    }

    private void tick(double timestamp) {
        tickListeners.forEach(tickListener -> tickListener.tick(timestamp));
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

    // Commands: Play, Pause, Rewind, SetSpeed(HLAfloat64BE), LoadScenario(HLAASCIIstring), SaveScenario(HLAASCIIstring)
    public void sendCgfControlInteraction(CgfCommand cgfCommand) {
        CgfControlInteraction cgfControlInteraction = new CgfControlInteraction();
        cgfControlInteraction.setCommand(cgfCommand);

        EntityIdentifierStruct receivingEntity = new EntityIdentifierStruct();
        // 0xFFFF for all federates
        receivingEntity.setFederateIdentifier(new FederateIdentifierStruct(0, 0));
        cgfControlInteraction.setCommandRecipient(receivingEntity);

        logger.debug("Sending CgfControlInteraction with command " + cgfCommand.getCommand().name() + ".");
        cgfControlInteraction.sendInteraction();
    }

}


