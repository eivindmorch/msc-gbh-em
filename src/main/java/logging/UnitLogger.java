package logging;

import model.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Writer;

import java.util.ArrayList;
import java.util.List;

import static util.Values.*;

public class UnitLogger {

    private final Logger logger = LoggerFactory.getLogger(UnitLogger.class);

    private volatile List<UnitDataWriter> unitDataWriters = new ArrayList<>();

    public void reset() {
        for (UnitDataWriter unitDataWriter : unitDataWriters) {
            unitDataWriter.closeWriters();
        }
        unitDataWriters = new ArrayList<>();
    }

    public void register(Unit unit) {
        unitDataWriters.add(new UnitDataWriter(unit));
        logger.info("Unit " + unit.getRole().name() + " was registered for data logging.");
    }

    public void logAllRegisteredUnits() {
        for (UnitDataWriter unitDataWriter : unitDataWriters) {
            unitDataWriter.writeDataToFile();
        }
    }

    private class UnitDataWriter {

        Unit unit;

        private Writer rawDataWriter;
        private Writer processedDataWriter;

        UnitDataWriter(Unit unit) {
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
}
