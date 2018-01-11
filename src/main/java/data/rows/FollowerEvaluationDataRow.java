package data.rows;

import util.Geometer;

import java.util.List;

public class FollowerEvaluationDataRow extends DataRow {

    private String dataSetName = "FollowerEvaluationData";

    private double distanceToTarget;

    public FollowerEvaluationDataRow() {
    }

    @Override
    public void setValues(List<String> csvElements) {
        this.timestamp = Double.valueOf(csvElements.get(0));
        this.distanceToTarget = Double.valueOf(csvElements.get(1));
    }

    public void setValues(double timestamp, RawDataRow rawDataRow1, RawDataRow rawDataRow) {
        this.timestamp = timestamp;
        this.distanceToTarget = Geometer.distance(rawDataRow1.getLla(), rawDataRow.getLla());
    }

    @Override
    public String getDataSetName() {
        return this.dataSetName;
    }

    @Override
    public String getHeader() {
        return "timestamp, distance to target";
    }

    @Override
    public String getValuesAsCsvString() {
        return timestamp + ", " + distanceToTarget;
    }

    @Override
    public String toString() {
        return "Timestamp: " + timestamp + ", " + "Distance to target: " + distanceToTarget;
    }
}
