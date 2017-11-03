import hla.rti1516e.exceptions.*;
import no.ffi.hlalib.HlaLib;
import no.ffi.hlalib.HlaObject;
import no.ffi.hlalib.events.HlaObjectRemovedEvent;
import no.ffi.hlalib.events.HlaObjectUpdatedEvent;
import no.ffi.hlalib.listeners.HlaObjectListener;
import no.ffi.hlalib.listeners.HlaObjectUpdateListener;
import no.ffi.hlalib.listeners.TimeManagementListener;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import no.ffi.hlalib.services.FederateManager;

import java.util.concurrent.atomic.AtomicInteger;


public class Logger implements Runnable, HlaObjectListener, HlaObjectUpdateListener, TimeManagementListener {

    private FederateManager federateManager;
    private volatile Unit follower = new Unit(Unit.Role.FOLLOWER);
    private volatile Unit target = new Unit(Unit.Role.TARGET);

    private transient boolean running = true;
    private volatile boolean constrained = false;
    private volatile boolean regulated = false;

    public Logger() {
        System.setProperty("hlalib-config-filepath", "src/main/resources/HlaLibConfig.xml");

        federateManager = HlaLib.init();
        federateManager.addTimeManagementListener(this);

        // Add listener for physical  entities
        PhysicalEntityObject.addHlaObjectListener(this);

        federateManager.init();
        System.out.println("Init complete");
    }

    @Override
    public void remoteObjectDiscovered(HlaObject object) {
        PhysicalEntityObject physicalEntity = (PhysicalEntityObject) object;
        physicalEntity.addObjectUpdateListener(this);
        physicalEntity.requestUpdateOnAllAttributes();

        System.out.println("An object was added.");
    }

    @Override
    public void hlaObjectUpdated(HlaObjectUpdatedEvent updatedEvent) {
        if (updatedEvent.getHlaObject() instanceof PhysicalEntityObject) {
            PhysicalEntityObject physicalEntity = (PhysicalEntityObject) updatedEvent.getHlaObject();

            String markingString = physicalEntity.getMarking().getMarking();

                // Update units
                if (markingString.equals(Unit.Role.FOLLOWER.name())) {
                    follower.setValues(physicalEntity);
                } else if (markingString.equals(Unit.Role.TARGET.name())) {
                    target.setValues(physicalEntity);
                }
//            }
            System.out.println("Object updated: " + markingString);
        }
    }

    @Override
    public void remoteObjectRemoved(HlaObjectRemovedEvent removedEvent) {}

    @Override
    public void localObjectCreated(HlaObject object) {}

    @Override
    public void localObjectRemoved(HlaObject hlaObject) {}

    private void increment(AtomicInteger atomicInteger) {
        atomicInteger.set(atomicInteger.get() + 10);
    }

    public void run() {
        while (running) {
            try {
                federateManager.requestTimeAdvanceAndBlock(federateManager.getLogicalTime() + 1.0);
            } catch (SaveInProgress saveInProgress) {
                saveInProgress.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RestoreInProgress restoreInProgress) {
                restoreInProgress.printStackTrace();
            } catch (InvalidLogicalTime invalidLogicalTime) {
                invalidLogicalTime.printStackTrace();
            } catch (InTimeAdvancingState inTimeAdvancingState) {
                inTimeAdvancingState.printStackTrace();
            } catch (LogicalTimeAlreadyPassed logicalTimeAlreadyPassed) {
                logicalTimeAlreadyPassed.printStackTrace();
            } catch (RTIexception rtIexception) {
                rtIexception.printStackTrace();
            }
            tick(federateManager.getLogicalTime());
        }
    }

    private void tick(double timestamp) {
        follower.writeToFile(timestamp);
        target.writeToFile(timestamp);
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
//
    private void checkConditions() {
        if (regulated && constrained) {
            new Thread(this).start();
        }
    }

    public static void main(String[] args) {
        Logger logger = new Logger();
    }
}


