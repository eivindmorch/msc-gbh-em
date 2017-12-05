package training;

public class Util {

    static double distanceFitness(double exampleDistance, double iterationDistance, double exponent) {
        return Math.pow(Math.abs(exampleDistance - iterationDistance), exponent);
    }
}
