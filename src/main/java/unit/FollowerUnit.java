package unit;

import data.FollowerEvaluationData;
import data.FollowerProcessedData;
import data.RawData;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;


public class FollowerUnit extends Unit {

    private FollowerProcessedData followerProcessedData;
    private FollowerEvaluationData followerEvaluationData;
    private Unit target;

    public FollowerUnit(ObjectInstanceHandle handle, String marking, Unit target) {
        super(marking, handle);
        this.target = target;

        this.followerProcessedData = new FollowerProcessedData();
        this.dataSets.add(followerProcessedData);

        this.followerEvaluationData = new FollowerEvaluationData();
        this.dataSets.add(followerEvaluationData);
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
        RawData targetRawData = new RawData(timestamp, targetLocation, targetVelocity);
        followerProcessedData.setValues(timestamp, this.getRawData(), targetRawData);
    }

    private void updateEvaluationData(double timestamp, PhysicalEntityObject targetPhysicalEntity) {
        WorldLocationStruct targetLocation = targetPhysicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct targetVelocity = targetPhysicalEntity.getSpatial().getDeadReckonedVelocity();
        RawData targetRawData = new RawData(timestamp, targetLocation, targetVelocity);
        followerEvaluationData.setValues(timestamp, this.getRawData(), targetRawData);
    }

    public FollowerProcessedData getFollowerProcessedData() {
        return followerProcessedData;
    }

    public Unit getTarget() {
        return target;
    }
}
