package experiments.experiment1.unit;

import experiments.experiment1.data.rows.RawDataRow;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import core.unit.Unit;

public class Experiment1Unit extends Unit {

    private RawDataRow rawDataRow;

    public Experiment1Unit(String marking, String identifier, ObjectInstanceHandle handle) {
        super(marking, identifier, handle);

        this.rawDataRow = new RawDataRow();
        this.dataRows.add(rawDataRow);
    }

    @Override
    public void updateData(double timestamp) {
        updateRawData(timestamp);
    }

    private void updateRawData(double timestamp) {
        PhysicalEntityObject physicalEntity = PhysicalEntityObject.getAllPhysicalEntitys().get(getHandle());
        WorldLocationStruct location = physicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct velocity = physicalEntity.getSpatial().getDeadReckonedVelocity();
        rawDataRow.setValues(timestamp, location, velocity);
    }

    public RawDataRow getRawDataRow() {
        return rawDataRow;
    }
}
