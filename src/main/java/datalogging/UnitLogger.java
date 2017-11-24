package datalogging;

import model.Unit;
import util.Writer;

import static util.Values.*;

class UnitLogger {

    Unit unit;

    private Writer rawDataWriter;
    private Writer processedDataWriter;

    UnitLogger(Unit unit) {
        this.unit = unit;
        String roleFolder = unit.getRole().name().toLowerCase() + "/";
        this.rawDataWriter = new Writer(rawDataPath + roleFolder, rawDataHeader);
        this.processedDataWriter = new Writer(processedDataPath + roleFolder, processedDataHeader);
    }

    void writeDataToFile() {
        rawDataWriter.writeLine(unit.getRawData().getValuesAsCsvString());
        processedDataWriter.writeLine(unit.getProcessedData().getValuesAsCsvString());
    }

    void closeWriters() {
        rawDataWriter.close();
        processedDataWriter.close();
    }
}
