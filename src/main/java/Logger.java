import hla.rti1516e.ObjectInstanceHandle;
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
import util.Values.*;

import java.util.ArrayList;
import java.util.List;


public class Logger implements Runnable, HlaObjectListener, HlaObjectUpdateListener, TimeManagementListener {

    private FederateManager federateManager;
    private volatile List<Unit> units = new ArrayList<>();

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
    }

    @Override
    public void hlaObjectUpdated(HlaObjectUpdatedEvent updatedEvent) {
        if (updatedEvent.getHlaObject() instanceof PhysicalEntityObject) {
            PhysicalEntityObject physicalEntity = (PhysicalEntityObject) updatedEvent.getHlaObject();

            String markingString = physicalEntity.getMarking().getMarking();

            try {
                Role role = Role.valueOf(markingString);
                ObjectInstanceHandle handle = physicalEntity.getObjectInstanceHandle();
                units.add(new Unit(handle, role));
                System.out.println("Object " + markingString + " was added with handle " + handle);
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
        if (units.size() == 2 ) {
            updateUnits(timestamp);

            for (Unit unit : units) {
                unit.writeToFile();
            }
        }
    }

    private void updateUnits(double timestamp) {
        for (Unit unit : units) {
            PhysicalEntityObject physicalEntity = PhysicalEntityObject.getAllPhysicalEntitys().get(unit.handle);
            // TODO Test if phycialEntity is updated at all times
            unit.setRawData(timestamp, physicalEntity);
        }
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
            new Thread(this).start();
        }
    }

    public static void main(String[] args) {
        Logger logger = new Logger();
    }

    public void reset() {
        // Close unit writers
        for (Unit unit : units) {
            unit.closeWriters();
        }
        // Reset unit list
        units = new ArrayList<>();

        // TODO
        // Reset federation timestamp to 0
        // Reset scenario
    }

}


