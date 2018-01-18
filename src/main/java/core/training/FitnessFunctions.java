package core.training;

public abstract class FitnessFunctions {

    public static double distanceFitness(double exampleDistance, double iterationDistance, double exponent) {
        return Math.pow(Math.abs(exampleDistance - iterationDistance), exponent);
    }
}
