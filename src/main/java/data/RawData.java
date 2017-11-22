package data;

import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class RawData extends Data {

    public Vector3D posVector;     // Position
    public Vector3D velVector;     // Velocity

    public RawData(double timestamp, WorldLocationStruct position, VelocityVectorStruct velocity) {
        setValues(timestamp, position, velocity);
    }

    public void setValues(double timestamp, WorldLocationStruct position, VelocityVectorStruct velocity) {
        this.timestamp = timestamp;
        // (X=Height, Y=West/East, Z=North/South)
        this.posVector = new Vector3D(position.getY(), position.getZ(), position.getX());
        this.velVector = new Vector3D(velocity.getYVelocity(), velocity.getZVelocity(), velocity.getXVelocity());
    }

    public String getValuesAsCsvString() {
        return timestamp + ", " + posVector.getX() + ", " + posVector.getY() + ", " + posVector.getZ() + ", "
                + velVector.getX() + ", " + velVector.getY() + ", " + velVector.getZ();
    }

    @Override
    public String toString() {
        return "Timestamp: " + timestamp + ", Position:" + posVector + ", Velocity:" + velVector;
    }
}
