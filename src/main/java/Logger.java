import no.ffi.hlalib.HlaLib;
import no.ffi.hlalib.HlaObject;
import no.ffi.hlalib.events.HlaObjectRemovedEvent;
import no.ffi.hlalib.events.HlaObjectUpdatedEvent;
import no.ffi.hlalib.listeners.HlaObjectListener;
import no.ffi.hlalib.listeners.HlaObjectUpdateListener;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import no.ffi.hlalib.services.FederateManager;


public class Logger implements HlaObjectListener, HlaObjectUpdateListener {

    private FederateManager federateManager;
    private Unit follower, target;

    public Logger() {
        System.setProperty("hlalib-config-filepath", "src/main/resources/HlaLibConfig.xml");

        federateManager = HlaLib.init();

        // Add listener for physical  entities
        PhysicalEntityObject.addHlaObjectListener(this);

        federateManager.init();
    }

    @Override
    public void remoteObjectDiscovered(HlaObject object) {
        PhysicalEntityObject physicalEntity = (PhysicalEntityObject) object;
        physicalEntity.addObjectUpdateListener(this);
        physicalEntity.requestUpdateOnAllAttributes();

        // TODO Not guaranteed that Marking exists
        String markingString = physicalEntity.getMarking().getMarking();

        if (markingString.equals(Unit.Role.FOLLOWER.name())) {
            // TODO Init unit
        } else if (markingString.equals(Unit.Role.TARGET.name())) {
            // TODO Init unit
        }

        System.out.println("An object was added.");
    }

    @Override
    public void hlaObjectUpdated(HlaObjectUpdatedEvent updatedEvent) {
        System.out.println("An object was added");
        if (updatedEvent.getHlaObject() instanceof PhysicalEntityObject) {
            PhysicalEntityObject physicalEntity = (PhysicalEntityObject) updatedEvent.getHlaObject();

            if (updatedEvent.getAttributeHandles().contains(physicalEntity.markingHandle)) {
                String markingString = physicalEntity.getMarking().getMarking();
                System.out.println("Object name: " + markingString);

                if (markingString.equals(Unit.Role.FOLLOWER.name())) {
                    // TODO Update follower unit
                } else if (markingString.equals(Unit.Role.TARGET.name())) {
                    // TODO Update target unit
                }
            }
        }
    }

    @Override
    public void remoteObjectRemoved(HlaObjectRemovedEvent removedEvent) {}

    @Override
    public void localObjectCreated(HlaObject object) {}

    @Override
    public void localObjectRemoved(HlaObject hlaObject) {}
}
