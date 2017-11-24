package datalogging;

import data.ProcessedData;
import data.RawData;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import util.Writer;

import static util.Values.*;

public class Unit {

    private final ObjectInstanceHandle handle;
    private final Role role;

    private RawData rawData;
    private ProcessedData processedData;

    private boolean hasValues;

    private Writer rawDataWriter;
    private Writer processedDataWriter;

    Unit(ObjectInstanceHandle handle, Role role) {
        this.handle = handle;
        this.role = role;

        String roleFolder = role.name().toLowerCase() + "/";
        this.rawDataWriter = new Writer(rawDataPath + roleFolder, rawDataHeader);
        this.processedDataWriter = new Writer(processedDataPath + roleFolder, processedDataHeader);
    }

    void updateProcessedData(double timestamp, Unit otherUnit) {
        if (processedData == null) {
            processedData = new ProcessedData(timestamp, this.rawData, otherUnit.rawData);
        } else {
            processedData.setValues(timestamp, this.rawData, otherUnit.rawData);
        }
    }

    void setRawData(double timestamp, PhysicalEntityObject physicalEntity) {
        WorldLocationStruct location = physicalEntity.getSpatial().getDeadReckonedLocation();
        VelocityVectorStruct velocity = physicalEntity.getSpatial().getDeadReckonedVelocity();
        if (rawData == null) {
            rawData = new RawData(timestamp, location, velocity);
            hasValues = true;
        } else {
            rawData.setValues(timestamp, location, velocity);
        }
    }

    void writeDataToFile() {
        rawDataWriter.writeLine(rawData.getValuesAsCsvString());
        processedDataWriter.writeLine(processedData.getValuesAsCsvString());
    }

    @Override
    public String toString() {
        return role + ", "  + rawData.toString();
    }

    void closeWriters() {
        rawDataWriter.close();
        processedDataWriter.close();
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
