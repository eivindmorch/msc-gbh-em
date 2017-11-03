import no.ffi.hlalib.datatypes.fixedRecordData.OrientationStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.WorldLocationStruct;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;

public class Unit {

    private double x, y, z;         // Position
    private float phi, psi, theta;  // Orientation

    final Role role;
    public enum Role {
        FOLLOWER, TARGET
    }

    private Writer rawDataWriter;
    private String rawDataHeader = "x, y, z, phi, psi, theta";
    private Writer processedDataWriter;
    private String processedDataHeader = "";

    public Unit(Role role, PhysicalEntityObject physicalEntity) {
        this.role = role;
        setValues(physicalEntity);

        this.rawDataWriter = new Writer(role.name(), rawDataHeader);
        this.processedDataWriter = new Writer(role.name(), processedDataHeader);
    }

    private void setValues(double x, double y, double z, float phi, float psi, float theta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.phi = phi;
        this.psi = psi;
        this.theta = theta;
        System.out.println(x + ", " + y + ", " + z + ", " + phi + ", " + psi + ", " + theta);
    }

    void setValues(PhysicalEntityObject physicalEntity) {
        WorldLocationStruct location = physicalEntity.getSpatial().getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        OrientationStruct orientation = physicalEntity.getSpatial().getOrientation();
        float phi = orientation.getPhi();
        float psi = orientation.getPsi();
        float theta = orientation.getTheta();

        setValues(x, y, z, phi, psi, theta);
    }

    public String getRawData() {
        return x + "," + y + "," + z + "," + phi + "," + psi  + "," + theta;
    }

    public String getProcessedData() {
        // TODO
        return "";
    }

    public void writeToFile(double tick) {
        rawDataWriter.writeLine(tick + getRawData());
        processedDataWriter.writeLine(tick + getProcessedData());
    }
}
