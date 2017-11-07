package data;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class ProcessedData {
    
    double distance, angle;

    public ProcessedData(RawData rawData1, RawData rawData2) {
        setValues(rawData1, rawData2);
    }
    
    public void setValues(RawData rawData1, RawData rawData2) {
        distance = getDistance(rawData1.posVector, rawData2.posVector);
        angle = getMovementAngle(rawData1, rawData2);
    }

    public String getValuesAsCsvString() {
        return distance + ", " + angle;
    }

    // Euclidean distance
    double getDistance(Vector3D followerPosVector, Vector3D targetPosVector) {
        return followerPosVector.distance(targetPosVector);
    }

    // Relative angle of other units movement direction, with 0 being toward and 180 being away from this unit
    double getMovementAngle(RawData rawData1, RawData rawData2) {
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

    @Override
    public String toString() {
        return getValuesAsCsvString();
    }
}
