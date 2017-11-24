package model;

import data.ProcessedData;
import data.RawData;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;

import static util.Values.*;

public class Unit {

    private final ObjectInstanceHandle handle;
    private final Role role;

    private RawData rawData;
    private ProcessedData processedData;

    private boolean hasValues;

    public Unit(ObjectInstanceHandle handle, Role role) {
        this.handle = handle;
        this.role = role;
    }

    public void updateProcessedData(double timestamp, Unit otherUnit) {
        if (processedData == null) {
            processedData = new ProcessedData(timestamp, this.rawData, otherUnit.rawData);
        } else {
            processedData.setValues(timestamp, this.rawData, otherUnit.rawData);
        }
    }

    public void setRawData(double timestamp, PhysicalEntityObject physicalEntity) {
        WorldLocationStruct location = physicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct velocity = physicalEntity.getSpatial().getDeadReckonedVelocity();
        if (rawData == null) {
            rawData = new RawData(timestamp, location, velocity);
            hasValues = true;
        } else {
            rawData.setValues(timestamp, location, velocity);
        }
    }

    @Override
    public String toString() {
        return role + ", "  + rawData.toString();
    }

    public ObjectInstanceHandle getHandle() {
        return handle;
    }

    public Role getRole() {
        return role;
    }

    public RawData getRawData() {
        return rawData;
    }

    public ProcessedData getProcessedData() {
        return processedData;
    }

    public boolean hasValues() {
        return hasValues;
    }
}
