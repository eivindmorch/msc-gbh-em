package data;

public class LlaData {

    private final double latitude, longitude, height;

    // TODO Possibly store in RawData
    public LlaData(double latitude, double longitude, double height) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Lat: " + latitude + ", Lon: " + longitude + ", Height: " + height;
    }
}
