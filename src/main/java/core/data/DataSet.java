package core.data;

import core.data.rows.DataRow;
import core.util.Reader;

import java.util.ArrayList;

import static core.settings.SystemSettings.intraResourcesExamplesFolderPath;

public class DataSet<D extends DataRow> {

    private String scenarioPath;
    private ArrayList<D> dataRows;
    private String unitMarking;
    private int numOfTicks;

    public DataSet(Class<D> dataRowClass, String exampleName) {
        dataRows = new ArrayList<>();

        Reader reader = new Reader(intraResourcesExamplesFolderPath + exampleName);
        reader.readLine(); // Ignore start time

        scenarioPath = reader.readLine().split(": ")[1];
        unitMarking = reader.readLine().split(": ")[1];
        reader.readLine(); // Ignore header

        String line;
        while ((line = reader.readLine()) != null) {
            try {
                D dataRow = dataRowClass.newInstance();
                dataRow.setValues(Reader.stringToCsvList(line));
                dataRows.add(dataRow);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                break;
            }
        }
        this.numOfTicks = dataRows.size();
    }

    public String getScenarioPath() {
        return scenarioPath;
    }

    public ArrayList<D> getDataRows() {
        return dataRows;
    }

    public String getUnitMarking() {
        return unitMarking;
    }

    public String getDataSetName() {
        D dataRow = dataRows.get(0);
        if (dataRow != null) {
            return dataRow.getDataSetName();
        }
        // TODO Change?
        return "No name";
    }

    public int getNumOfTicks() {
        return numOfTicks;
    }
}
