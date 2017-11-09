import data.ProcessedData;
import util.Reader;

import java.util.List;

import static util.Values.*;

public class Trainer {

    private ProcessedData exampleData, iterationData;
    private double fitness;

    private void run() {
        Reader exampleDataReader = new Reader(exampleDataFilePath);
        Reader iterationDataReader = new Reader(iterationDataFilePath);

        List<String> exampleDataLine = exampleDataReader.readLine();
        List<String> iterationDataLine = iterationDataReader.readLine();

        while (exampleDataLine != null && iterationDataLine != null) {
            ProcessedData exampleData = new ProcessedData(exampleDataLine);
            ProcessedData iterationData = new ProcessedData(iterationDataLine);

            fitness += distanceFitness(exampleData.getDistance(), iterationData.getDistance());

            exampleDataLine = exampleDataReader.readLine();
            iterationDataLine = iterationDataReader.readLine();
            System.out.println(fitness);
        }
    }

    private double distanceFitness(double exampleDistance, double iterationDistance) {
        return Math.pow(Math.abs(exampleDistance - iterationDistance), 2);
    }

    public static void main(String[] args) {
        Trainer trainer = new Trainer();
        trainer.run();
    }


}
