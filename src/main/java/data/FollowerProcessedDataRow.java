package data;

import util.Geometer;
import util.exceptions.IllegalArgumentCombinationException;

import java.util.List;

public class FollowerProcessedDataRow extends DataRow {

    private String dataSetName = "FollowerProcessedData";

    private double distanceToTarget;
    private Double targetMovementAngleRelativeToFollowerPosition;

    public FollowerProcessedDataRow() {
    }

    public FollowerProcessedDataRow(List<String> csvElements) {
        this.timestamp = Double.valueOf(csvElements.get(0));
        this.distanceToTarget = Double.valueOf(csvElements.get(1));
        this.targetMovementAngleRelativeToFollowerPosition = Double.valueOf(csvElements.get(2));
    }

    public void setValues(double timestamp, RawDataRow rawDataRow1, RawDataRow rawDataRow) {
        this.timestamp = timestamp;
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
        return this.dataSetName;
    }

    @Override
    public String getHeader() {
        return "timestamp, distance to target, target movement angle relative to follower position";
    }

    public String getValuesAsCsvString() {
        return timestamp + ", " + distanceToTarget + ", " + targetMovementAngleRelativeToFollowerPosition;
    }

    @Override
    public String toString() {
        return "Timestamp: " + timestamp + ", " + "Distance to target: " + distanceToTarget + ", " + "Target movement angle relative to follower position: " + targetMovementAngleRelativeToFollowerPosition;
    }
}
