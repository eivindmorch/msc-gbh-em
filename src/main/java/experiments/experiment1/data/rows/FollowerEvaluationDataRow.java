package experiments.experiment1.data.rows;

import core.data.rows.DataRow;
import core.model.Lla;
import core.util.ToStringBuilder;

import java.util.List;

public class FollowerEvaluationDataRow extends DataRow {

    public static String dataSetName = "FollowerEvaluationData";

    private Lla lla;

    public FollowerEvaluationDataRow() {
    }

    @Override
    public void setValues(List<String> csvElements) {
        this.setTimestamp(Double.valueOf(csvElements.get(0)));
        this.lla = new Lla(
                Double.valueOf(csvElements.get(1)),
                Double.valueOf(csvElements.get(2)),
                Double.valueOf(csvElements.get(3))
        );
    }

    public void setValues(double timestamp, RawDataRow followerRawDataRow, RawDataRow targetRawDataRow) {
        this.setTimestamp(timestamp);
        this.lla = new Lla(
                followerRawDataRow.getLla().getLatitude(),
                followerRawDataRow.getLla().getLongitude(),
                followerRawDataRow.getLla().getAltitude()
        );
    }

    @Override
    public String getDataSetName() {
        return FollowerEvaluationDataRow.dataSetName;
    }

    @Override
    public String getHeader() {
        return "timestamp, latitude, longitude, altitude";
    }

    public Lla getLla() {
        return lla;
    }

    @Override
    public String getValuesAsCsvString() {
        return this.getTimestamp() + ", " + lla.getLatitude() + ", " + lla.getLongitude() + ", " + lla.getAltitude();
    }

    @Override
    public String toString() {
        return ToStringBuilder.toStringBuilder(this)
                .add("timestamp", this.getTimestamp())
                .add("position", lla)
                .toString();
    }
}
