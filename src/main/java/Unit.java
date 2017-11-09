import data.ProcessedData;
import data.RawData;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import util.Writer;

import static util.Values.*;

public class Unit {

    final Role role;
    public enum Role {
        FOLLOWER, TARGET
    }

    private RawData rawData;
    private ProcessedData processedData;

    private Writer rawDataWriter;
    private Writer processedDataWriter;

    public boolean hasValues;


    Unit(Role role) {
        this.role = role;

        String roleFolder = role.name().toLowerCase() + "/";
        this.rawDataWriter = new Writer(rawDataPath + roleFolder, rawDataHeader);
        this.processedDataWriter = new Writer(processedDataPath + roleFolder, processedDataHeader);
    }

    void updateProcessedData(Unit otherUnit) {
        if (processedData == null) {
            processedData = new ProcessedData(this.rawData, otherUnit.rawData);
            System.out.println(processedData);
        } else {
            processedData.setValues(this.rawData, otherUnit.rawData);
            System.out.println(processedData);
        }
    }

    void setRawData(PhysicalEntityObject physicalEntity) {
        WorldLocationStruct location = physicalEntity.getSpatial().getLocation();
        VelocityVectorStruct velocity = physicalEntity.getSpatial().getVelocity();
        if (rawData == null) {
            rawData = new RawData(location, velocity);
            hasValues = true;
        } else {
            rawData.setValues(location, velocity);
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
}
