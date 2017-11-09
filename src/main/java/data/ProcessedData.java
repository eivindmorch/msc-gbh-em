package data;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class ProcessedData extends Data {

    private double distance, angle;

    public ProcessedData(RawData rawData1, RawData rawData2) {
        setValues(rawData1, rawData2);
    }
    
    public void setValues(RawData rawData1, RawData rawData2) {
        distance = calculateDistance(rawData1.posVector, rawData2.posVector);
        angle = calculateMovementAngle(rawData1, rawData2);
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

    @Override
    public String toString() {
        return getValuesAsCsvString();
    }
}
