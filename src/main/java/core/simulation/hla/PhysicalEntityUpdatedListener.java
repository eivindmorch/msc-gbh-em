package core.simulation.hla;

import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;

public interface PhysicalEntityUpdatedListener {

    void physicalEntityUpdated(PhysicalEntityObject physicalEntity);

    void physicalEntityRemoved(ObjectInstanceHandle objectInstanceHandle);

}
