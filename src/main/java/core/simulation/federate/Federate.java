package core.simulation.federate;

import core.simulation.SimSettings;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.exceptions.RTIexception;
import no.ffi.hlalib.HlaLib;
import no.ffi.hlalib.HlaObject;
import no.ffi.hlalib.datatypes.enumeratedData.CommandType;
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

import static core.util.SystemUtil.sleepMilliseconds;

public class Federate implements Runnable, HlaObjectListener, HlaObjectUpdateListener, TimeManagementListener {

    private static Federate instance;

    private final Logger logger = LoggerFactory.getLogger(Federate.class);

    private FederateManager federateManager;

    private transient boolean running = true;
    private volatile boolean constrained = false;
    private volatile boolean regulated = false;

    private ArrayList<TickListener> tickListeners;
    private ArrayList<PhysicalEntityUpdatedListener> physicalEntityUpdatedListeners;

    private volatile boolean holdTimeAdvancement = true;
    private final Object TIME_ADVANCE_LOCK = new Object();

    public volatile int unitsDiscovered = 0;

    private Federate() {
        System.setProperty("hlalib-config-filepath", "src/main/resources/HlaLibConfig.xml");
    }

    public static Federate getInstance() {
        if (instance == null) {
            instance = new Federate();
        }
        return instance;
    }

    public void start() {
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
            logger.debug("Remote object discovered: " + physicalEntity.getMarking());
            physicalEntity.addObjectUpdateListener(this);
            physicalEntity.requestUpdateOnAllAttributes();
            unitsDiscovered++;
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
    public void remoteObjectRemoved(HlaObjectRemovedEvent removedEvent) {
        ObjectInstanceHandle objectInstanceHandle = removedEvent.getObjectInstanceHandle();
        physicalEntityUpdatedListeners.forEach(
                physicalEntityUpdatedListener -> physicalEntityUpdatedListener.physicalEntityRemoved(objectInstanceHandle)
        );
    }

    @Override
    public void localObjectCreated(HlaObject object) {}

    @Override
    public void localObjectRemoved(HlaObject hlaObject) {
    }

    @Override
    public void run() {
        while (running) {
            while (holdTimeAdvancement) {
                synchronized (TIME_ADVANCE_LOCK) {
                    try {
                        TIME_ADVANCE_LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                federateManager.requestTimeAdvanceAndBlock(federateManager.getTimestamp());
                tick(federateManager.getLogicalTime());
            } catch (InterruptedException | RTIexception saveInProgress) {
                saveInProgress.printStackTrace();
            }
            if (SimSettings.simulationTickDelayInMilliseconds > 0) {
                sleepMilliseconds(SimSettings.simulationTickDelayInMilliseconds);
            }
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

    public void sendCgfPlayInteraction() {
        CgfCommand cgfCommand = new CgfCommand();
        cgfCommand.setCommand(CommandType.Play);
        sendCgfControlInteraction(cgfCommand);
    }

    // TODO Ask Martin
    // Causes the RTI to tick when the engine is paused. Do not use.
    public void sendCgfPauseInteraction() {
        CgfCommand cgfCommand = new CgfCommand();
        cgfCommand.setCommand(CommandType.Pause);
        sendCgfControlInteraction(cgfCommand);
    }

    public void sendCgfRewindInteraction() {
        unitsDiscovered = 0;
        CgfCommand cgfCommand = new CgfCommand();
        cgfCommand.setCommand(CommandType.Rewind);
        sendCgfControlInteraction(cgfCommand);
    }

    public void sendCgfLoadScenarioInteraction(String scenarioPath) {
        unitsDiscovered = 0;
        CgfCommand cgfCommand = new CgfCommand();
        cgfCommand.setCommand(CommandType.LoadScenario);
        cgfCommand.getLoadScenario().setString(scenarioPath);
        sendCgfControlInteraction(cgfCommand);
    }

    // Possible commands:
    // Play, Pause, Rewind, SetSpeed(HLAfloat64BE), LoadScenario(HLAASCIIstring), SaveScenario(HLAASCIIstring)
    private void sendCgfControlInteraction(CgfCommand cgfCommand) {
        CgfControlInteraction cgfControlInteraction = new CgfControlInteraction();
        cgfControlInteraction.setCommand(cgfCommand);

        EntityIdentifierStruct receivingEntity = new EntityIdentifierStruct();
        // 0 = wildcard
        // TODO Should use 0xFFFF
        receivingEntity.setFederateIdentifier(new FederateIdentifierStruct(0, 0));
        cgfControlInteraction.setCommandRecipient(receivingEntity);

        logger.info("Sending CgfControlInteraction with command " + cgfCommand.getCommand().name() + ".");
        cgfControlInteraction.sendInteraction();
    }

    public void holdTimeAdvancement() {
        logger.info("Holding time advancement.");
        holdTimeAdvancement = true;
    }

    public void enableTimeAdvancement() {
        logger.info("Enabling time advancement.");
        holdTimeAdvancement = false;
        synchronized (TIME_ADVANCE_LOCK) {
            TIME_ADVANCE_LOCK.notifyAll();
        }
    }

}


