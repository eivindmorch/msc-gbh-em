package core.data;

import core.data.rows.DataRow;
import core.util.Reader;

import java.util.ArrayList;

public class ExampleDataSet<T extends DataRow> {

    private String scenarioName;
    private ArrayList<T> dataRows;

    //TODO move
    private String intraResourcesExamplesFolderPath = "data/examples/";

    public ExampleDataSet(Class<T> dataRowClass, String exampleName) {
        Reader reader = new Reader(intraResourcesExamplesFolderPath + exampleName);
        reader.readLine(); // Ignore start time
        scenarioName = reader.readLine().split(": ")[1]; // Scenario name
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                T dataRow = dataRowClass.newInstance();
                dataRow.setValues(Reader.stringToCsvList(line));
                dataRows.add(dataRow);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public ArrayList<T> getDataRows() {
        return dataRows;
    }

}
