package core.unit;

import core.data.rows.DataRow;
import core.util.SystemUtil;
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

    static void register(Unit unit) {
        unitDataWriters.put(unit.getHandle(), new UnitDataWriter(unit));
        logger.info("Unit registered for logging -- Marking: " + unit.getMarking());
    }

    static void remove(Unit unit) {
        unitDataWriters.remove(unit.getHandle()).closeWriters();
        logger.info("Unit removed from logging -- Marking: " + unit.getMarking());
    }

    public static void logAllRegisteredUnits() {
        for (UnitDataWriter unitDataWriter : unitDataWriters.values()) {
            unitDataWriter.writeDataToFile();
        }
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
                        SystemUtil.getDataFileIntraResourcesFolderPath(
                                SystemStatus.currentTrainingEpoch,
                                SystemStatus.currentTrainingExampleDataSetIndex,
                                SystemStatus.currentTrainingChromosome
                        ) + unit.getMarking() + "/",
                        dataRow.getDataSetName() + ".csv"
                ));
            }
            writeMetaDataToFile();
            writeHeadersToFile();
        }

        void writeMetaDataToFile() {
            for (int i = 0; i < dataWriters.size(); i++) {
                Writer dataWriter = dataWriters.get(i);
                dataWriter.writeLine("# System start time: " + SystemStatus.startTime);
                dataWriter.writeLine("# Scenario path: " + SystemStatus.currentScenario);
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
