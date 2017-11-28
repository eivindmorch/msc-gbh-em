package unit;

import data.Data;
import data.RawData;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;

import java.util.ArrayList;
import java.util.List;


public class Unit {

    private String marking;
    private final ObjectInstanceHandle handle;

    List<Data> dataSets;
    private RawData rawData;

    public Unit(String marking, ObjectInstanceHandle handle) {
        this.marking = marking;
        this.handle = handle;
        this.dataSets = new ArrayList<>();
        this.rawData = new RawData();
        this.dataSets.add(rawData);
    }

    public void updateData(double timestamp) {
        PhysicalEntityObject physicalEntity = PhysicalEntityObject.getAllPhysicalEntitys().get(handle);
        setRawData(timestamp, physicalEntity);
    }

    private void setRawData(double timestamp, PhysicalEntityObject physicalEntity) {
        WorldLocationStruct location = physicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct velocity = physicalEntity.getSpatial().getDeadReckonedVelocity();
        rawData.setValues(timestamp, location, velocity);
    }

    @Override
    public String toString() {
        return marking + ", "  + rawData.toString();
    }

    public ObjectInstanceHandle getHandle() {
        return handle;
    }

    public RawData getRawData() {
        return rawData;
    }

    public List<Data> getDataSets() {
        return dataSets;
    }

    public String getMarking() {
        return marking;
    }

}
