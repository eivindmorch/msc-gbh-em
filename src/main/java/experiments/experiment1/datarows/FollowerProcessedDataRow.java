package experiments.experiment1.datarows;

import core.data.DataRow;
import core.util.Geometer;
import core.util.ToStringBuilder;
import core.util.exceptions.IllegalArgumentCombinationException;

import java.util.List;

public class FollowerProcessedDataRow extends DataRow {

    public static String dataSetName = "FollowerProcessedData";

    private double distanceToTarget;
    private Double targetMovementAngleRelativeToFollowerPosition;

    public FollowerProcessedDataRow() {
    }

    @Override
    public void setValues(List<String> csvElements) {

    }

    public void setValues(double timestamp, RawDataRow rawDataRow1, RawDataRow rawDataRow) {
        this.setTimestamp(timestamp);
        this.distanceToTarget = Geometer.distance(rawDataRow1.getLla(), rawDataRow.getLla());
        this.targetMovementAngleRelativeToFollowerPosition = calculateMovementAngleRelativeToMyPosition(rawDataRow1, rawDataRow);
    }

    private Double calculateMovementAngleRelativeToMyPosition(RawDataRow rawDataRow1, RawDataRow rawDataRow){
        // GeoLine "between" from unit2 to unit1
        // Angle = azimuth of "between" - azimuth of unit2 "movement"
        double angleOfLineFromUnit2ToUnit1;
        try {
            angleOfLineFromUnit2ToUnit1 = Geometer.absoluteBearing(rawDataRow.getLla(), rawDataRow1.getLla());
        } catch (IllegalArgumentCombinationException e) {
            return null;
        }
        if (rawDataRow.getMovementAngle() == null) {
            return null;
        }
        double angle = angleOfLineFromUnit2ToUnit1 - rawDataRow.getMovementAngle();
        return Geometer.normalise360Angle(angle);
    }

    public double getDistanceToTarget() {
        return distanceToTarget;
    }

    public Double getTargetMovementAngleRelativeToFollowerPosition() {
        return targetMovementAngleRelativeToFollowerPosition;
    }

    @Override
    public String getDataSetName() {
        return FollowerProcessedDataRow.dataSetName;
    }

    @Override
    public String getHeader() {
        return "timestamp, distance to target, target movement angle relative to follower position";
    }

    public String getValuesAsCsvString() {
        return this.getTimestamp() + ", " + distanceToTarget + ", " + targetMovementAngleRelativeToFollowerPosition;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toStringBuilder(this)
                .add("timestamp", this.getTimestamp())
                .add("distanceToTarget", this.getDistanceToTarget())
                .add("targetMovementAngleRelativeToFollowerPosition", targetMovementAngleRelativeToFollowerPosition)
                .toString();
    }
}
