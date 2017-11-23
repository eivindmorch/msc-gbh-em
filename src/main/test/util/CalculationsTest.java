package util;

import data.LlaData;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculationsTest {


//    @Test
//    void ecefToLlaTest1() {
//        Vector3D ecefVector = new Vector3D(3139085.752378, 5441286.460927, 1101245.907901);
//        LlaData llaData = Calculations.ecefToLla(ecefVector);
//        assertEquals(1, Calculations.ecefToLla(ecefVector));
//    }
//
//    @Test
//    void ecefToLlaTest2() {
//        Vector3D ecefVector = new Vector3D(0.1, 0.1, 0.1);
//        assertEquals(1, Calculations.ecefToLla(ecefVector));
//    }

    @Test
    void geolibTest() {
        Vector3D ecefVector1 = new Vector3D(3139085.752378, 5441286.460927, 1101245.907901);
        Vector3D ecefVector2 = new Vector3D(3139085.512, 5441286.98, 1101245.23);

        LlaData lla1 = Calculations.ecefToLla(ecefVector1);
        LlaData lla2 = Calculations.ecefToLla(ecefVector2);

        assertEquals(1, Calculations.geodlib(lla1, lla2));
    }

    @Test
    void absoluteBearingTest() {
        Vector3D ecefVector1 = new Vector3D(3139085.752378, 5441286.460927, 1101245.907901);
        Vector3D ecefVector2 = new Vector3D(3139085.512, 5441286.98, 1101245.23);

//        assertEquals(1, Calculations.absoluteBearing(ecefVector1, ecefVector2));
    }

}