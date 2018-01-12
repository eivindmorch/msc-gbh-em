package data.rows.experiment1;

import data.rows.DataRow;
import model.Lla;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import util.Geometer;
import util.exceptions.IllegalArgumentCombinationException;

import java.util.List;

public class RawDataRow extends DataRow {

    private String dataSetName = "RawData";

    private Lla lla;
    private Double movementAngle;

    public RawDataRow() {
    }

    public RawDataRow(double timestamp, WorldLocationStruct position, VelocityVectorStruct velocity) {
        setValues(timestamp, position, velocity);
    }

    @Override
    public void setValues(List<String> csvElements) {

    }

    public void setValues(double timestamp, WorldLocationStruct position, VelocityVectorStruct velocity) {
        this.setTimestamp(timestamp);
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
        return angle;
    }

    public Lla getLla() {
        return lla;
    }

    public Double getMovementAngle() {
        return movementAngle;
    }

    @Override
    public String getDataSetName() {
        return this.dataSetName;
    }

    @Override
    public String getHeader() {
        return "timestamp, latitude, longitude, altitude, movement angle";
    }

    public String getValuesAsCsvString() {
        return this.getTimestamp() + ", " + lla.getLatitude() + ", " + lla.getLongitude() + ", " + lla.getAltitude() + ", "
                + movementAngle;
    }

    @Override
    public String toString() {
        return getDataSetName() + " {Timestamp: " + this.getTimestamp() + ", Position:" + lla + ", Movement angle:" + movementAngle + "}" ;
    }
}
