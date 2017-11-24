package data;

import exceptions.IllegalArgumentCombinationException;
import models.Lla;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import util.Geometer;

public class RawData extends Data {

    private Lla lla;
    private Double movementAngle;

    public RawData(double timestamp, WorldLocationStruct position, VelocityVectorStruct velocity) {
        setValues(timestamp, position, velocity);
    }

    public void setValues(double timestamp, WorldLocationStruct position, VelocityVectorStruct velocity) {
        this.timestamp = timestamp;
        this.lla = Geometer.ecefToLla(position.getX(), position.getY(), position.getZ());
        this.movementAngle = calculateMovementAzimuthAngle(position, velocity);
    }

    private Double calculateMovementAzimuthAngle(WorldLocationStruct position, VelocityVectorStruct velocity) {
        Vector3D posVector = new Vector3D(position.getX(), position.getY(), position.getZ());
        Vector3D velVector = new Vector3D(velocity.getXVelocity(), velocity.getYVelocity(), velocity.getZVelocity());

        Vector3D nextPosVector = posVector.add(velVector);
        Lla nextLla = Geometer.ecefToLla(nextPosVector);

        double angle;
        try {
            angle = Geometer.absoluteBearing(this.lla, nextLla);
        } catch (IllegalArgumentCombinationException e) {
            return null;
        }
        System.out.println("Movement angle: " + angle);
        return angle;
    }

    public Lla getLla() {
        return lla;
    }

    public Double getMovementAngle() {
        return movementAngle;
    }

    public String getValuesAsCsvString() {
        return timestamp + ", " + lla.getLatitude() + ", " + lla.getLongitude() + ", " + lla.getAltitude() + ", "
                + movementAngle;
    }

    @Override
    public String toString() {
        return "Timestamp: " + timestamp + ", Position:" + lla + ", Movement angle:" + movementAngle ;
    }
}
