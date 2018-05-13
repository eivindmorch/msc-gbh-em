package core.unit;

import core.data.DataRow;
import core.simulation.SimController;
import hla.rti1516e.ObjectInstanceHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.util.SystemStatus;
import core.util.Writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class UnitLogger {

    private static final Logger logger = LoggerFactory.getLogger(UnitLogger.class);

    private static volatile HashMap<ObjectInstanceHandle, UnitDataWriter> unitDataWriters = new HashMap<>();

    private static String intraResourcesWritingDirectory;

    static void register(Unit unit) {
        unitDataWriters.put(unit.getHandle(), new UnitDataWriter(unit));
        logger.info("Unit registered for logging: " + unit);
    }

    static void remove(Unit unit) {
        unitDataWriters.remove(unit.getHandle()).closeWriters();
        logger.info("Unit removed from logging: " + unit);
    }

    public static void logAllRegisteredUnits() {
        for (UnitDataWriter unitDataWriter : unitDataWriters.values()) {
            unitDataWriter.writeDataToFile();
        }
    }

    public static void setIntraResourcesWritingDirectory(String path) {
        intraResourcesWritingDirectory = path;
    }

    public static void reset() {
        logger.info("Resetting UnitDataWriters.");
        for (UnitDataWriter unitDataWriter : unitDataWriters.values()) {
            unitDataWriter.closeWriters();
        }
        unitDataWriters = new HashMap<>();
    }

    private static class UnitDataWriter {

        Unit unit;
        private List<Writer> dataWriters;

        UnitDataWriter(Unit unit) {
            this.unit = unit;

            dataWriters = new ArrayList<>();
            for (DataRow dataRow : unit.getDataRows()) {
                dataWriters.add(new Writer(
                        intraResourcesWritingDirectory + unit.getMarking() + "/",
                        dataRow.getDataSetName() + ".csv"
                ));
            }
            writeMetaDataToFile();
            writeHeadersToFile();
        }

        void writeMetaDataToFile() {
            for (Writer dataWriter : dataWriters) {
                dataWriter.writeLine("# System start time: " + SystemStatus.START_TIME_STRING);
                dataWriter.writeLine("# Scenario path: " + SimController.getInstance().getCurrentScenario());
                dataWriter.writeLine("# Unit marking: " + unit.getMarking());
            }
        }

        void writeHeadersToFile() {
            for (int i = 0; i < dataWriters.size(); i++) {
                dataWriters.get(i).writeLine(unit.getDataRows().get(i).getHeader());
            }
        }

        void writeDataToFile() {
            for (int i = 0; i < dataWriters.size(); i++) {
                dataWriters.get(i).writeLine(unit.getDataRows().get(i).getValuesAsCsvString());
            }
        }

        void closeWriters() {
            for (Writer writer  : dataWriters) {
                writer.close();
            }
        }
    }
}
