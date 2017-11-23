package data;

public class LlaData {

    private final double latitude, longitude, altitude;

    // TODO Possibly store in RawData
    public LlaData(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    @Override
    public String toString() {
        return "Lat: " + latitude + ", Lon: " + longitude + ", Altitude: " + altitude;
    }
}
