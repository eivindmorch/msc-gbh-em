package util;

import data.LlaData;
import net.sf.geographiclib.Constants;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicLine;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Calculations {

    public static double absoluteBearing(LlaData lla1, LlaData lla2) {
        double lat1 = lla1.getLatitude();
        double long1 = lla1.getLongitude();

        double lat2 = lla2.getLatitude();
        double long2 = lla2.getLongitude();

        Geodesic geodesic = new Geodesic(Constants.WGS84_a, Constants.WGS84_f);
        GeodesicLine geoLine = geodesic.InverseLine(lat1, long1, lat2, long2);
        double azimuth = geoLine.Azimuth();
        return azimuth;
    }

    public static LlaData ecefToLla(Vector3D ecefVector) {
        double x = ecefVector.getX();
        double y = ecefVector.getY();
        double z = ecefVector.getZ();

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

        return new LlaData(latitude, longitude, altitude);
    }

    private static double replaceZeroWithMinValue(double val) {
        if (val == 0) {
            return Double.MIN_VALUE;
        }
        return val;
    }

}
