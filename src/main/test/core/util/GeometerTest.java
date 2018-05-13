package core.util;

import core.data.Lla;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.Test;
import core.util.exceptions.IllegalArgumentCombinationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static core.util.Geometer.getDestinationPointFromAzimuthAngle;

class GeometerTest {

    @Test
    void ecefToLlaTest() {
        Lla lla = new Lla(10.008938452906103, 60.01929267705622, 136.54059425368905);
        Vector3D ecefVector = new Vector3D(3139085.752378, 5441286.460927, 1101245.907901);
        assertEquals(lla, Geometer.ecefToLla(ecefVector));
    }

    @Test
    void absoluteBearingTest() throws IllegalArgumentCombinationException {
        Vector3D ecefVector1 = new Vector3D(3139085.752378, 5441286.460927, 1101245.907901);
        Vector3D ecefVector2 = new Vector3D(3139085.512, 5441286.98, 1101245.23);

        Lla lla1 = Geometer.ecefToLla(ecefVector1);
        Lla lla2 = Geometer.ecefToLla(ecefVector2);

        assertEquals(147.17407566330704, Geometer.absoluteBearing(lla1, lla2));
    }

    @Test
    void getDestinationPointFromAzimuthAngleTest() {
        Lla lla = new Lla(10.008938452906103, 60.01929267705622, 136.54059425368905);
        double azimuthAngle = 120;

        assertEquals(new Lla(9.963724267920348, 60.09827247228762, 136.54059425368905), getDestinationPointFromAzimuthAngle(lla, azimuthAngle, 10000));
    }

}