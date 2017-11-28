package data;

import util.exceptions.IllegalArgumentCombinationException;
import util.Geometer;

import java.util.List;

// TODO Rename
public class ProcessedData extends Data {

    private double distance;
    private Double otherUnitMovementAngleRelativeToMyPosition;

    public ProcessedData() {
    }

    public ProcessedData(List<String> csvElements) {
        this.timestamp = Double.valueOf(csvElements.get(0));
        this.distance = Double.valueOf(csvElements.get(1));
        this.otherUnitMovementAngleRelativeToMyPosition = Double.valueOf(csvElements.get(2));
    }

    public void setValues(double timestamp, RawData rawData1, RawData rawData2) {
        this.timestamp = timestamp;
        this.distance = Geometer.distance(rawData1.getLla(), rawData2.getLla());
        this.otherUnitMovementAngleRelativeToMyPosition = calculateMovementAngleRelativeToMyPosition(rawData1, rawData2);
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

    public double getDistance() {
        return distance;
    }

    public Double getOtherUnitMovementAngleRelativeToMyPosition() {
        return otherUnitMovementAngleRelativeToMyPosition;
    }

    @Override
    public String getHeader() {
        return "timestamp, distance, target angle relative to my position";
    }

    public String getValuesAsCsvString() {
        return timestamp + ", " + distance + ", " + otherUnitMovementAngleRelativeToMyPosition;
    }

    @Override
    public String toString() {
        return "Timestamp: " + timestamp + ", " + "Distance: " + distance + ", " + "Angle: " + otherUnitMovementAngleRelativeToMyPosition;
    }
}
