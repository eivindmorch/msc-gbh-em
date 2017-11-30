package data;

import util.exceptions.IllegalArgumentCombinationException;
import util.Geometer;

import java.util.List;

// TODO Rename
public class FollowerProcessedData extends Data {

    private double distanceToTarget;
    private Double targetMovementAngleRelativeToFollowerPosition;

    public FollowerProcessedData() {
    }

    public FollowerProcessedData(List<String> csvElements) {
        this.timestamp = Double.valueOf(csvElements.get(0));
        this.distanceToTarget = Double.valueOf(csvElements.get(1));
        this.targetMovementAngleRelativeToFollowerPosition = Double.valueOf(csvElements.get(2));
    }

    public void setValues(double timestamp, RawData rawData1, RawData rawData2) {
        this.timestamp = timestamp;
        this.distanceToTarget = Geometer.distance(rawData1.getLla(), rawData2.getLla());
        this.targetMovementAngleRelativeToFollowerPosition = calculateMovementAngleRelativeToMyPosition(rawData1, rawData2);
    }

    private Double calculateMovementAngleRelativeToMyPosition(RawData rawData1, RawData rawData2){
        // GeoLine "between" from unit2 to unit1
        // Angle = azimuth of "between" - azimuth of unit2 "movement"
        double angleOfLineFromUnit2ToUnit1;
        try {
            angleOfLineFromUnit2ToUnit1 = Geometer.absoluteBearing(rawData2.getLla(), rawData1.getLla());
        } catch (IllegalArgumentCombinationException e) {
            return null;
        }
        if (rawData2.getMovementAngle() == null) {
            return null;
        }
        double angle = angleOfLineFromUnit2ToUnit1 - rawData2.getMovementAngle();
        return Geometer.normalise360Angle(angle);
    }

    public double getDistanceToTarget() {
        return distanceToTarget;
    }

    public Double getTargetMovementAngleRelativeToFollowerPosition() {
        return targetMovementAngleRelativeToFollowerPosition;
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
