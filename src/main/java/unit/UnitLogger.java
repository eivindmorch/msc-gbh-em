package unit;

import data.DataRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SystemMode;
import util.SystemStatus;
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
        logger.info("Unit registered for logging -- Marking: " + unit.getMarking());
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

            dataWriters = new ArrayList<>();
            for (DataRow dataRow : unit.getDataRows()) {
                // TODO Check if terminology is correct
                dataWriters.add(new Writer(
                        getDataFileStorageFolder() + unit.getMarking(),
                        dataRow.getDataSetName() + ".csv"
                ));
            }
            writeMetaDataToFile();
            writeHeadersToFile();
        }

        private String getDataFileStorageFolder() {
            StringBuilder stringBuilder = new StringBuilder("data/");
            stringBuilder.append(SystemStatus.systemMode.name().toLowerCase()).append("/");
            stringBuilder.append(SystemStatus.startTime).append("/");
            if (SystemStatus.systemMode == SystemMode.TRAINING) {
                stringBuilder
                        .append("epoch").append(SystemStatus.currentTrainingEpoch).append("/")
                        .append("scenario").append(SystemStatus.currentTrainingScenario).append("/")
                        .append("chromosome").append(SystemStatus.currentTrainingChromosome).append("/");
            }
            return stringBuilder.toString();
        }

        void writeMetaDataToFile() {
            for (int i = 0; i < dataWriters.size(); i++) {
                Writer dataWriter = dataWriters.get(i);
                dataWriter.writeLine("# System start time: " + SystemStatus.startTime);
                dataWriter.writeLine("# Scenario: " + SystemStatus.currentScenario);
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
