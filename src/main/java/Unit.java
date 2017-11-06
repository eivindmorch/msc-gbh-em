import no.ffi.hlalib.datatypes.fixedRecordData.VelocityVectorStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Unit {

    Vector3D posVector;     // Position
    Vector3D velVector;     // Velocity

    final Role role;
    public enum Role {
        FOLLOWER, TARGET
    }

    private Writer rawDataWriter;
    private String rawDataPath = "raw/";
    private String rawDataHeader = "timestamp, x, y, z, phi, psi, theta";

    private Writer processedDataWriter;
    private String processedDataPath = "processed/";
    private String processedDataHeader = "";

    Unit(Role role) {
        this.role = role;

        String roleFolder = role.name().toLowerCase() + "/";
        this.rawDataWriter = new Writer(rawDataPath + roleFolder, rawDataHeader);
        this.processedDataWriter = new Writer(processedDataPath + roleFolder, processedDataHeader);

        this.posVector = new Vector3D(0, 0, 0);
        this.velVector = new Vector3D(0, 0, 0);
    }

    private void setValues(WorldLocationStruct position, VelocityVectorStruct velocity) {
        this.posVector = new Vector3D(position.getX(), position.getY(), position.getZ());
        this.velVector = new Vector3D(velocity.getXVelocity(), velocity.getYVelocity(), velocity.getZVelocity());
    }

    void setValues(PhysicalEntityObject physicalEntity) {
        WorldLocationStruct location = physicalEntity.getSpatial().getLocation();
        VelocityVectorStruct velocity = physicalEntity.getSpatial().getVelocity();
        setValues(location, velocity);
    }

    String getRawData() {
        return posVector.getX() + ", " + posVector.getY() + ", " + posVector.getZ() + ", "
                + velVector.getX() + ", " + velVector.getY() + ", " + velVector.getZ();
    }

    String getProcessedData(Unit otherUnit) {
        double distance = getDistance(otherUnit);
        double angle = getMovementAngle(otherUnit);
        return distance + ", " + angle;
    }

    // Euclidean distance
    double getDistance(Unit otherUnit) {
        return posVector.distance(otherUnit.posVector);
    }

    // Relative angle of other units movement direction, with 0 being toward and 180 being away from this unit
    double getMovementAngle(Unit otherUnit) {
        Vector3D vectorBetweenUnits = this.posVector.subtract(otherUnit.posVector);
        // TODO Convert to using 3D
        double dot = vectorBetweenUnits.dotProduct(otherUnit.velVector);
        double det = vectorBetweenUnits.getX() * otherUnit.velVector.getY() - vectorBetweenUnits.getY() * otherUnit.velVector.getX();
        double angle = Math.atan2(dot, det);
        angle = angle * 180 / Math.PI;
        angle -= 90;
        while (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    void writeToFile(double tick, Unit otherUnit) {
        System.out.println(getProcessedData(otherUnit));
        rawDataWriter.writeLine(tick + ", " + getRawData());
        processedDataWriter.writeLine(tick + getProcessedData(otherUnit));
    }

    @Override
    public String toString() {
        return this.posVector + ", " + this.velVector;
    }
}
