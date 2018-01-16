package core.data;

import core.data.rows.DataRow;
import core.util.Reader;

import java.util.ArrayList;

import static core.settings.SystemSettings.intraResourcesExamplesFolderPath;

public class ExampleDataSet<D extends DataRow> {

    private String scenarioPath;
    private ArrayList<D> dataRows;
    private int numOfTicks;

    public ExampleDataSet(Class<D> dataRowClass, String exampleName) {
        dataRows = new ArrayList<>();

        Reader reader = new Reader(intraResourcesExamplesFolderPath + exampleName);
        reader.readLine(); // Ignore start time

        scenarioPath = reader.readLine().split(": ")[1]; // Scenario name

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

    public int getNumOfTicks() {
        return numOfTicks;
    }
}
