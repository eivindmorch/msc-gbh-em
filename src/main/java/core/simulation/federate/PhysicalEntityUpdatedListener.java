package core.simulation.federate;

import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;

public interface PhysicalEntityUpdatedListener {

    void physicalEntityUpdated(PhysicalEntityObject physicalEntity);

}
