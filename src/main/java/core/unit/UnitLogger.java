package core.unit;

import core.data.rows.DataRow;
import core.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.util.SystemMode;
import core.util.SystemStatus;
import core.util.Writer;

import java.util.ArrayList;
import java.util.List;


public abstract class UnitLogger {

    private static final Logger logger = LoggerFactory.getLogger(UnitLogger.class);

    private static volatile List<UnitDataWriter> unitDataWriters = new ArrayList<>();

    static void register(Unit unit) {
        unitDataWriters.add(new UnitDataWriter(unit));
        logger.info("Unit registered for logging -- Marking: " + unit.getMarking());
    }

    public static void logAllRegisteredUnits() {
        for (UnitDataWriter unitDataWriter : unitDataWriters) {
            unitDataWriter.writeDataToFile();
        }
    }

    public static void reset() {
        logger.info("Resetting UnitDataWriters.");
        for (UnitDataWriter unitDataWriter : unitDataWriters) {
            unitDataWriter.closeWriters();
        }
        unitDataWriters = new ArrayList<>();
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
                                SystemStatus.currentTrainingScenario,
                                SystemStatus.currentTrainingChromosome
                        ) + unit.getMarking(),
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
