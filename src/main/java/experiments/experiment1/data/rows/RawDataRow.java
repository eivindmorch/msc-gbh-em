package experiments.experiment1.data.rows;

import core.data.rows.DataRow;
import core.model.Lla;
import core.util.ToStringBuilder;
import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import core.util.Geometer;
import core.util.exceptions.IllegalArgumentCombinationException;

import java.util.List;

public class RawDataRow extends DataRow {

    public static String dataSetName = "RawData";

    private Lla lla;
    private Double movementAngle;

    public RawDataRow() {
    }

    public RawDataRow(double timestamp, WorldLocationStruct position, VelocityVectorStruct velocity) {
        setValues(timestamp, position, velocity);
    }

    @Override
    public void setValues(List<String> csvElements) {
        this.setTimestamp(Double.valueOf(csvElements.get(0)));
        this.lla = new Lla(
                Double.valueOf(csvElements.get(1)),
                Double.valueOf(csvElements.get(2)),
                Double.valueOf(csvElements.get(3))
        );
        if (!csvElements.get(4).equals("null")) {
            this.movementAngle = Double.valueOf(csvElements.get(4));
        }
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
        return RawDataRow.dataSetName;
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
        return ToStringBuilder.toStringBuilder(this)
                .add("timestamp", String.format("%5.3f", this.getTimestamp()))
                .add("position", lla)
                .add("movementAngle", movementAngle)
                .toString();
    }
}
