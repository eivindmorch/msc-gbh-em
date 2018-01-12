package core.training;

//TODO Rename to FitnessFunctions?
public class FitnessEvaluator {

    static double distanceFitness(double exampleDistance, double iterationDistance, double exponent) {
        return Math.pow(Math.abs(exampleDistance - iterationDistance), exponent);
    }
}
