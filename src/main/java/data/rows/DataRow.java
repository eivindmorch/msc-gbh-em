package data.rows;

import java.util.List;

public abstract class DataRow {

    double timestamp;

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public abstract String getDataSetName();

    public abstract String getHeader();

    public abstract String getValuesAsCsvString();

    public abstract void setValues(List<String> csvElements);
}
