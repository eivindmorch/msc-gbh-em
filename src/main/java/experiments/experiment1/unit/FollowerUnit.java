package experiments.experiment1.unit;

import experiments.experiment1.datarows.FollowerEvaluationDataRow;
import experiments.experiment1.datarows.FollowerProcessedDataRow;
import experiments.experiment1.datarows.RawDataRow;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;


public class FollowerUnit extends Experiment1Unit {

    private FollowerProcessedDataRow followerProcessedDataRow;
    private FollowerEvaluationDataRow followerEvaluationDataRow;
    private Experiment1Unit target;

    public FollowerUnit(String marking, String identifier, ObjectInstanceHandle handle, Experiment1Unit target) {
        super(marking, identifier, handle);
        this.target = target;

        this.followerProcessedDataRow = new FollowerProcessedDataRow();
        this.dataRows.add(followerProcessedDataRow);

        this.followerEvaluationDataRow = new FollowerEvaluationDataRow();
        this.dataRows.add(followerEvaluationDataRow);
    }

    @Override
    public void updateData(double timestamp) {
        PhysicalEntityObject targetPhysicalEntity = PhysicalEntityObject.getAllPhysicalEntitys().get(target.getHandle());
        updateRawData(timestamp);
        updateProcessedData(timestamp, targetPhysicalEntity);
        updateEvaluationData(timestamp, targetPhysicalEntity);
    }

    private void updateRawData(double timestamp) {
        PhysicalEntityObject physicalEntity = super.getPhysicalEntityObject();
        WorldLocationStruct location = physicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct velocity = physicalEntity.getSpatial().getDeadReckonedVelocity();
        getRawDataRow().setValues(timestamp, location, velocity);
    }

    private void updateProcessedData(double timestamp, PhysicalEntityObject targetPhysicalEntity) {
        WorldLocationStruct targetLocation = targetPhysicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct targetVelocity = targetPhysicalEntity.getSpatial().getDeadReckonedVelocity();
        RawDataRow targetRawDataRow = new RawDataRow(timestamp, targetLocation, targetVelocity);
        followerProcessedDataRow.setValues(timestamp, getRawDataRow(), targetRawDataRow);
    }

    private void updateEvaluationData(double timestamp, PhysicalEntityObject targetPhysicalEntity) {
        WorldLocationStruct targetLocation = targetPhysicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct targetVelocity = targetPhysicalEntity.getSpatial().getDeadReckonedVelocity();
        RawDataRow targetRawDataRow = new RawDataRow(timestamp, targetLocation, targetVelocity);
        followerEvaluationDataRow.setValues(timestamp, getRawDataRow(), targetRawDataRow);
    }

    public FollowerProcessedDataRow getFollowerProcessedDataRow() {
        return followerProcessedDataRow;
    }

    public Experiment1Unit getTarget() {
        return target;
    }
}
