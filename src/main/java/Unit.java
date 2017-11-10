import data.ProcessedData;
import data.RawData;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import util.Writer;

import static util.Values.*;

public class Unit {

    public final ObjectInstanceHandle handle;
    public final Role role;

    private RawData rawData;
    private ProcessedData processedData;

    private Writer rawDataWriter;
    private Writer processedDataWriter;

    public boolean hasValues;


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
        printData(role.name(), otherUnit.rawData);
    }

    void setRawData(double timestamp, PhysicalEntityObject physicalEntity) {
        WorldLocationStruct location = physicalEntity.getSpatial().getLocation();
        VelocityVectorStruct velocity = physicalEntity.getSpatial().getVelocity();
        System.out.println(physicalEntity.isReflected(physicalEntity.spatialHandle));
        if (rawData == null) {
            rawData = new RawData(timestamp, location, velocity);
            hasValues = true;
        } else {
            rawData.setValues(timestamp, location, velocity);
        }
    }

    void updateDataTimestamps(double timestamp) {
        rawData.setTimestamp(timestamp);
        processedData.setTimestamp(timestamp);
    }

    void writeToFile() {
        rawDataWriter.writeLine(rawData.getValuesAsCsvString());
        processedDataWriter.writeLine(processedData.getValuesAsCsvString());
    }

    @Override
    public String toString() {
        return role + ", "  + rawData.toString();
    }

    private void printData(String role, RawData otherUnitData) {
        System.out.println("---------------------");
        System.out.println(role);
        System.out.println(this.rawData);
        System.out.println(otherUnitData);
        System.out.println(processedData);
        System.out.println("---------------------");
    }
}
