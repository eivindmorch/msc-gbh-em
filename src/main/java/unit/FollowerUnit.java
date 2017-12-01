package unit;

import data.FollowerEvaluationDataRow;
import data.FollowerProcessedDataRow;
import data.RawDataRow;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;


public class FollowerUnit extends Unit {

    private FollowerProcessedDataRow followerProcessedDataRow;
    private FollowerEvaluationDataRow followerEvaluationDataRow;
    private Unit target;

    public FollowerUnit(ObjectInstanceHandle handle, String marking, Unit target) {
        super(marking, handle);
        this.target = target;

        this.followerProcessedDataRow = new FollowerProcessedDataRow();
        this.dataRows.add(followerProcessedDataRow);

        this.followerEvaluationDataRow = new FollowerEvaluationDataRow();
        this.dataRows.add(followerEvaluationDataRow);
    }

    @Override
    public void updateData(double timestamp) {
        super.updateData(timestamp);

        PhysicalEntityObject physicalEntity = PhysicalEntityObject.getAllPhysicalEntitys().get(target.getHandle());
        updateProcessedData(timestamp, physicalEntity);
        updateEvaluationData(timestamp, physicalEntity);
    }

    private void updateProcessedData(double timestamp, PhysicalEntityObject targetPhysicalEntity) {
        WorldLocationStruct targetLocation = targetPhysicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct targetVelocity = targetPhysicalEntity.getSpatial().getDeadReckonedVelocity();
        RawDataRow targetRawDataRow = new RawDataRow(timestamp, targetLocation, targetVelocity);
        followerProcessedDataRow.setValues(timestamp, this.getRawDataRow(), targetRawDataRow);
    }

    private void updateEvaluationData(double timestamp, PhysicalEntityObject targetPhysicalEntity) {
        WorldLocationStruct targetLocation = targetPhysicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct targetVelocity = targetPhysicalEntity.getSpatial().getDeadReckonedVelocity();
        RawDataRow targetRawDataRow = new RawDataRow(timestamp, targetLocation, targetVelocity);
        followerEvaluationDataRow.setValues(timestamp, this.getRawDataRow(), targetRawDataRow);
    }

    public FollowerProcessedDataRow getFollowerProcessedDataRow() {
        return followerProcessedDataRow;
    }

    public Unit getTarget() {
        return target;
    }
}
