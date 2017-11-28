package unit;

import data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Writer;

import java.util.ArrayList;
import java.util.List;


public class UnitLogger {

    private static final Logger logger = LoggerFactory.getLogger(UnitLogger.class);

    private static volatile List<UnitDataWriter> unitDataWriters = new ArrayList<>();

    public static void reset() {
        for (UnitDataWriter unitDataWriter : unitDataWriters) {
            unitDataWriter.closeWriters();
        }
        unitDataWriters = new ArrayList<>();
    }

    public static void register(Unit unit) {
        unitDataWriters.add(new UnitDataWriter(unit));
        logger.info("Unit " + unit.getMarking() + " was registered for data logging.");
    }

    public static void logAllRegisteredUnits() {
        for (UnitDataWriter unitDataWriter : unitDataWriters) {
            unitDataWriter.writeDataToFile();
        }
    }

    private static class UnitDataWriter {

        Unit unit;
        private List<Writer> dataWriters;


        UnitDataWriter(Unit unit) {
            this.unit = unit;
            String unitFolder = unit.getMarking() + "/";

            dataWriters = new ArrayList<>();
            for (Data dataSet : unit.getDataSets()) {
                dataWriters.add(new Writer("data/" + unitFolder + dataSet.getClass().getSimpleName() + "/", dataSet.getHeader()));
            }
        }

        void writeDataToFile() {
            for (int i = 0; i < dataWriters.size(); i++) {
                dataWriters.get(i).writeLine(unit.getDataSets().get(i).getValuesAsCsvString());
            }
        }

        void closeWriters() {
            for (Writer writer  : dataWriters) {
                writer.close();
            }
        }
    }
}
