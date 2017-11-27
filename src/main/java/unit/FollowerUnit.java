package unit;

import data.ProcessedData;
import data.RawData;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;


public class FollowerUnit extends Unit {

    private ProcessedData processedData;
    private Unit target;

    public FollowerUnit(ObjectInstanceHandle handle, String marking, Unit target) {
        super(marking, handle);
        this.target = target;
        this.processedData = new ProcessedData();
        this.dataSets.add(processedData);
    }

    @Override
    public void updateData(double timestamp) {
        super.updateData(timestamp);

        PhysicalEntityObject physicalEntity = PhysicalEntityObject.getAllPhysicalEntitys().get(target.getHandle());
        updateProcessedData(timestamp, physicalEntity);
    }

    private void updateProcessedData(double timestamp, PhysicalEntityObject targetPhysicalEntity) {
        WorldLocationStruct targetLocation = targetPhysicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct targetVelocity = targetPhysicalEntity.getSpatial().getDeadReckonedVelocity();
        RawData targetRawData = new RawData(timestamp, targetLocation, targetVelocity);
        processedData.setValues(timestamp, this.getRawData(), targetRawData);
    }

    public ProcessedData getProcessedData() {
        return processedData;
    }

    public Unit getTarget() {
        return target;
    }
}
