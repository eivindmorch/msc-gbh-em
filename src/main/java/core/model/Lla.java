package core.model;

import core.util.ToStringBuilder;

public class Lla {

    private final double latitude, longitude, altitude;

    public Lla(double latitude, double longitude, double altitude) {
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
        return ToStringBuilder.toStringBuilder(this)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .add("altitude", altitude)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        Lla oLla = (Lla) obj;
        return this.latitude == oLla.latitude && this.longitude == oLla.longitude && this.altitude == oLla.altitude;
    }
}
