package data;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;

public class ProcessedData extends Data {

    private double distance;
        private double angle;

    public ProcessedData(double timestamp, RawData rawData1, RawData rawData2) {
        setValues(timestamp, rawData1, rawData2);
    }

    public ProcessedData(List<String> csvElements) {
        this.timestamp = Double.valueOf(csvElements.get(0));
        this.distance = Double.valueOf(csvElements.get(1));
        this.angle = Double.valueOf(csvElements.get(2));
    }

    public void setValues(double timestamp, RawData rawData1, RawData rawData2) {
        this.timestamp = timestamp;
        this.distance = calculateDistance(rawData1.posVector, rawData2.posVector);
        this.angle = calculateMovementAngle(rawData1, rawData2);
    }

    // Euclidean distance
    private double calculateDistance(Vector3D followerPosVector, Vector3D targetPosVector) {
        return followerPosVector.distance(targetPosVector);
    }

    // Relative angle of other units movement direction, with 0 being toward and 180 being away from this unit
    private double calculateMovementAngle(RawData rawData1, RawData rawData2) {
        Vector3D vectorBetweenUnits = rawData1.posVector.subtract(rawData2.posVector);
        
        // TODO Convert to using 3D
        double dot = vectorBetweenUnits.dotProduct(rawData2.velVector);
        double det = vectorBetweenUnits.getX() * rawData2.velVector.getY() - vectorBetweenUnits.getY() * rawData2.velVector.getX();
        double angle = Math.atan2(dot, det);
        angle = angle * 180 / Math.PI;
        angle -= 90;
        while (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public String getValuesAsCsvString() {
        return timestamp + ", " + distance + ", " + angle;
    }

    public double getDistance() {
        return distance;
    }

    public double getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        return getValuesAsCsvString();
    }
}