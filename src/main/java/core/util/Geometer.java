package core.util;

import core.model.Lla;
import net.sf.geographiclib.Constants;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicLine;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import core.util.exceptions.IllegalArgumentCombinationException;

public abstract class Geometer {

    private static Geodesic geodesic = new Geodesic(Constants.WGS84_a, Constants.WGS84_f);

    public static double absoluteBearing(Lla fromLla, Lla toLla) throws IllegalArgumentCombinationException {
        if (fromLla.equals(toLla)) {
            throw new IllegalArgumentCombinationException();
        }
        GeodesicLine geoLine = geodesic.InverseLine(fromLla.getLatitude(), fromLla.getLongitude(), toLla.getLatitude(), toLla.getLongitude());
        double angle = geoLine.Azimuth();
        return normalise360Angle(angle);
    }

    public static Lla getDestinationPointFromAzimuthAngle(Lla lla, double azimuthAngle, double distanceInMeters) {
        GeodesicLine geoLine = geodesic.Line(lla.getLatitude(), lla.getLongitude(), azimuthAngle);
        GeodesicData geoData = geoLine.Position(distanceInMeters);
        return new Lla(geoData.lat2, geoData.lon2, lla.getAltitude());
    }

    public static double distance(Lla lla1, Lla lla2) {
        GeodesicLine geoLine = geodesic.InverseLine(lla1.getLatitude(), lla1.getLongitude(), lla2.getLatitude(), lla2.getLongitude());
        return geoLine.Distance();
    }

    public static Lla ecefToLla(Vector3D ecefVector) {
        return ecefToLla(ecefVector.getX(), ecefVector.getY(), ecefVector.getZ());
    }

    public static Lla ecefToLla(double x, double y, double z) {
        // Module WGS84
        double RADIUS = 6378137;
        double FLATTENING = 1/298.257223563;
        double POLAR_RADIUS = RADIUS*(1-FLATTENING);

        double radiusSqr = Math.pow(RADIUS, 2);
        double polarRadiusSqr = Math.pow(POLAR_RADIUS, 2);

        double e = Math.sqrt((radiusSqr - polarRadiusSqr)/ radiusSqr);
        double eprime = Math.sqrt((radiusSqr - polarRadiusSqr)/ polarRadiusSqr);

        double p = Math.sqrt(x*x + y*y);

        x = replaceZeroWithMinValue(x);
        y = replaceZeroWithMinValue(y);
        z = replaceZeroWithMinValue(z);
        p = replaceZeroWithMinValue(p);

        double theta = Math.atan((z * RADIUS)/(p * POLAR_RADIUS));

        double sintheta = Math.sin(theta);
        double costheta = Math.cos(theta);

        double num = z + eprime * eprime * POLAR_RADIUS * sintheta * sintheta * sintheta;
        double denom = p - e * e * RADIUS * costheta * costheta * costheta;

        //Now calculate LLA
        double latitude  = Math.atan(num/denom);
        double longitude = Math.atan(y/x);

        double sinlatitude = Math.sin(latitude);
        double Ndenom = Math.sqrt(1-e*e*sinlatitude*sinlatitude);
        double N =  RADIUS / Ndenom;

        double altitude  = (p / Math.cos(latitude)) - N;

        if (x < 0 && y < 0) {
            longitude = longitude - Math.PI;
        }
        if (x < 0 && y > 0) {
            longitude = longitude + Math.PI;
        }

        latitude = Math.toDegrees(latitude);
        longitude = Math.toDegrees(longitude);

        return new Lla(latitude, longitude, altitude);
    }

    private static double replaceZeroWithMinValue(double val) {
        if (val == 0) {
            return Double.MIN_VALUE;
        }
        return val;
    }

    public static double normalise360Angle(double angle) {
        while (angle >= 360) {
            angle -= 360;
        }
        while (angle < 0) {
            angle += 360;
        }
        return angle;
    }

}
