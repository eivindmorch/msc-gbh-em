package experiments.experiment1.data.rows;

import core.data.DataRow;
import core.util.Geometer;
import core.util.ToStringBuilder;

import java.util.List;

public class FollowerEvaluationDataRow extends DataRow {

    public static String dataSetName = "FollowerEvaluationData";

    private double distanceToTarget;

    public FollowerEvaluationDataRow() {
    }

    @Override
    public void setValues(List<String> csvElements) {
        this.setTimestamp(Double.valueOf(csvElements.get(0)));
        this.distanceToTarget = Double.valueOf(csvElements.get(1));
    }

    public void setValues(double timestamp, RawDataRow rawDataRow1, RawDataRow rawDataRow) {
        this.setTimestamp(timestamp);
        this.distanceToTarget = Geometer.distance(rawDataRow1.getLla(), rawDataRow.getLla());
    }

    @Override
    public String getDataSetName() {
        return FollowerEvaluationDataRow.dataSetName;
    }

    @Override
    public String getHeader() {
        return "timestamp, distance to target";
    }

    public double getDistanceToTarget() {
        return distanceToTarget;
    }

    @Override
    public String getValuesAsCsvString() {
        return this.getTimestamp() + ", " + distanceToTarget;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toStringBuilder(this)
                .add("timestamp", this.getTimestamp())
                .add("distanceToTarget", this.getDistanceToTarget())
                .toString();
    }
}
