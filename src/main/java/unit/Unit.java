package unit;

import data.rows.DataRow;
import hla.rti1516e.ObjectInstanceHandle;

import java.util.ArrayList;
import java.util.List;


public abstract class Unit {

    private String marking;
    private final ObjectInstanceHandle handle;

    public List<DataRow> dataRows; // Used for writing dataRows to file

    public Unit(String marking, ObjectInstanceHandle handle) {
        this.marking = marking;
        this.handle = handle;
        this.dataRows = new ArrayList<>();
    }

    public abstract void updateData(double timestamp);

    public String getMarking() {
        return marking;
    }

    public ObjectInstanceHandle getHandle() {
        return handle;
    }

    List<DataRow> getDataRows() {
        return dataRows;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Unit: " + marking);
        for (DataRow dataRow : dataRows) {
            sb.append("\n");
            sb.append("\t").append(dataRow.toString());
        }
        return sb.toString();
    }
}
