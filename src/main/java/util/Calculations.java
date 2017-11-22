package util;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Calculations {

    public static double calculate360AngleBetween(Vector3D vector1, Vector3D vector2) {
        double dot = vector1.dotProduct(vector2);
        double det = vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX();
        double angle = Math.atan2(dot, det);
        angle = Math.toDegrees(angle);

//        angle -= 90;
        while (angle < 0) {
            angle += 360;
        }

        return angle;
    }
}
