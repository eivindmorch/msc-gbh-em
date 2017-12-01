package unit;

import data.DataRow;
import data.RawDataRow;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;

import java.util.ArrayList;
import java.util.List;


public class Unit {

    private String marking;
    private final ObjectInstanceHandle handle;

    List<DataRow> dataRows;
    private RawDataRow rawDataRow;

    public Unit(String marking, ObjectInstanceHandle handle) {
        this.marking = marking;
        this.handle = handle;
        this.dataRows = new ArrayList<>();
        this.rawDataRow = new RawDataRow();
        this.dataRows.add(rawDataRow);
    }

    public void updateData(double timestamp) {
        PhysicalEntityObject physicalEntity = PhysicalEntityObject.getAllPhysicalEntitys().get(handle);
        setRawData(timestamp, physicalEntity);
    }

    private void setRawData(double timestamp, PhysicalEntityObject physicalEntity) {
        WorldLocationStruct location = physicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct velocity = physicalEntity.getSpatial().getDeadReckonedVelocity();
        rawDataRow.setValues(timestamp, location, velocity);
    }

    @Override
    public String toString() {
        return marking + ", "  + rawDataRow.toString();
    }

    public ObjectInstanceHandle getHandle() {
        return handle;
    }

    public RawDataRow getRawDataRow() {
        return rawDataRow;
    }

    public List<DataRow> getDataRows() {
        return dataRows;
    }

    public String getMarking() {
        return marking;
    }

}
