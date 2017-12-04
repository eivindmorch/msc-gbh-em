package simulation.federate;

import hla.rti1516e.exceptions.*;
import no.ffi.hlalib.HlaLib;
import no.ffi.hlalib.HlaObject;
import no.ffi.hlalib.events.HlaInteractionReceivedEvent;
import no.ffi.hlalib.events.HlaObjectRemovedEvent;
import no.ffi.hlalib.events.HlaObjectUpdatedEvent;
import no.ffi.hlalib.interactions.HLAinteractionRoot.StartResumeInteraction;
import no.ffi.hlalib.interactions.HLAinteractionRoot.StopFreezeInteraction;
import no.ffi.hlalib.listeners.HlaInteractionListener;
import no.ffi.hlalib.listeners.HlaObjectListener;
import no.ffi.hlalib.listeners.HlaObjectUpdateListener;
import no.ffi.hlalib.listeners.TimeManagementListener;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import no.ffi.hlalib.services.FederateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class Federate implements Runnable, HlaObjectListener, HlaObjectUpdateListener, TimeManagementListener, HlaInteractionListener {

    private final Logger logger = LoggerFactory.getLogger(Federate.class);

    private FederateManager federateManager;

    private transient boolean running = true;
    private volatile boolean constrained = false;
    private volatile boolean regulated = false;

    private ArrayList<TickListener> tickListeners;
    private ArrayList<PhysicalEntityUpdatedListener> physicalEntityUpdatedListeners;

    public Federate() {
        System.setProperty("hlalib-config-filepath", "src/main/resources/HlaLibConfig.xml");
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
                federateManager.requestTimeAdvanceAndBlock(federateManager.getTimestamp());
            } catch (InterruptedException | RTIexception saveInProgress) {
                saveInProgress.printStackTrace();
            }
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

    public void addTickListener(TickListener tickListener) {
        tickListeners.add(tickListener);
    }

    public void addPhysicalEntityUpdatedListener(PhysicalEntityUpdatedListener physicalEntityUpdatedListener) {
        physicalEntityUpdatedListeners.add(physicalEntityUpdatedListener);
    }

    @Override
    public void interactionReceived(HlaInteractionReceivedEvent receivedEvent) {
        System.out.println(receivedEvent.getInteraction().getFomName());
//        if (receivedEvent.getInteraction() instanceof StartResumeInteraction) {
//            StartResumeInteraction startResumeInteraction = (StartResumeInteraction) receivedEvent.getInteraction();
//            System.out.println();
//            System.out.println("RECEIVED StartResumeInteraction with following data:");
//            System.out.println("Originating entity: " + startResumeInteraction.getOriginatingEntity().getFederateIdentifier());
//            System.out.println("Receiving entity: " + startResumeInteraction.getReceivingEntity().getFederateIdentifier());
//
//            System.out.println("originatingEntityHandle: " + startResumeInteraction.originatingEntityHandle);
//            System.out.println("receivingEntityHandle: " + startResumeInteraction.receivingEntityHandle);
//            System.out.println("Request identifier: " + startResumeInteraction.getRequestIdentifier());
//            System.out.println("Start time: " + startResumeInteraction.getSimulationTime());
//
//
//        } else if (receivedEvent.getInteraction() instanceof  StopFreezeInteraction) {
//            StopFreezeInteraction stopFreezeInteraction = (StopFreezeInteraction) receivedEvent.getInteraction();
//            System.out.println();
//            System.out.println("RECEIVED StopFreezeInteraction with following data:");
//            System.out.println("Originating entity: " + stopFreezeInteraction.getOriginatingEntity().getFederateIdentifier());
//            System.out.println("Receiving entity: " + stopFreezeInteraction.getReceivingEntity().getFederateIdentifier());
//
//            System.out.println("originatingEntityHandle: " + stopFreezeInteraction.originatingEntityHandle);
//            System.out.println("receivingEntityHandle: " + stopFreezeInteraction.receivingEntityHandle);
//            System.out.println("Request identifier: " + stopFreezeInteraction.getRequestIdentifier());
//        }
    }


}


