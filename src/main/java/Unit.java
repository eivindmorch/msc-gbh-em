public class Unit {

    private double x, y, z;         // Position
    private float phi, psi, theta;  // Orientation

    public final Role role;
    public enum Role {
        FOLLOWER, TARGET
    }

    private Writer rawDataWriter;
    private String rawDataHeader = "x, y, z, phi, psi, theta";
    private Writer processedDataWriter;
    private String processedDataHeader = "";

    public Unit(Role role, double x, double y, double z, float phi, float psi, float theta) {
        this.role = role;

        this.x = x;
        this.y = y;
        this.z = z;
        this.phi = phi;
        this.psi = psi;
        this.theta = theta;

        this.rawDataWriter = new Writer(role.name(), rawDataHeader);
        this.processedDataWriter = new Writer(role.name(), processedDataHeader);
    }

    public String getRawData() {
        return x + "," + y + "," + z + "," + phi + "," + psi  + "," + theta;
    }

    public String getProcessedData() {
        // TODO
        return "";
    }
}
