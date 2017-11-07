package data;

import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class RawData {

    Vector3D posVector;     // Position
    Vector3D velVector;     // Velocity

    public RawData(WorldLocationStruct position, VelocityVectorStruct velocity) {
        setValues(position, velocity);
    }

    public void setValues(WorldLocationStruct position, VelocityVectorStruct velocity) {
        this.posVector = new Vector3D(position.getX(), position.getY(), position.getZ());
        this.velVector = new Vector3D(velocity.getXVelocity(), velocity.getYVelocity(), velocity.getZVelocity());
    }

    public String getValuesAsCsvString() {
        return posVector.getX() + ", " + posVector.getY() + ", " + posVector.getZ() + ", "
                + velVector.getX() + ", " + velVector.getY() + ", " + velVector.getZ();
    }

    @Override
    public String toString() {
        return this.posVector + ", " + this.velVector;
    }
}
