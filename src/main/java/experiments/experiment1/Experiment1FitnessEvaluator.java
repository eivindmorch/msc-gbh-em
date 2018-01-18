package experiments.experiment1;

import core.data.DataSet;
import core.data.rows.DataRow;
import core.training.FitnessEvaluator;
import core.training.FitnessFunctions;
import experiments.experiment1.data.rows.FollowerEvaluationDataRow;

import java.util.ArrayList;

public class Experiment1FitnessEvaluator implements FitnessEvaluator {

    @Override
    public <D extends DataRow> ArrayList<Double> evaluate(DataSet<D> exampleDataSet, DataSet<D> chromosomeDataSet) throws Exception {
        if (exampleDataSet.getNumOfTicks() != chromosomeDataSet.getNumOfTicks()) {
            // TODO Log warning
        }
        if (!exampleDataSet.getDataSetName().equals(FollowerEvaluationDataRow.dataSetName)
                || !chromosomeDataSet.getDataSetName().equals(FollowerEvaluationDataRow.dataSetName)) {
            throw new Exception("Incompatible datasets: "
                    + exampleDataSet.getDataSetName()
                    + ", " + chromosomeDataSet.getDataSetName()
            );
        }



        DataSet<FollowerEvaluationDataRow> followerEvaluationExampleDataSet = (DataSet<FollowerEvaluationDataRow>) exampleDataSet;
        DataSet<FollowerEvaluationDataRow> followerEvaluationChromosomeDataSet = (DataSet<FollowerEvaluationDataRow>) chromosomeDataSet;

        ArrayList<Double> fitness = new ArrayList<>();
        fitness.add(0.);

        int numOfTicks = Math.min(exampleDataSet.getNumOfTicks(), chromosomeDataSet.getNumOfTicks());
        for (int i = 0; i < numOfTicks; i++) {
            FollowerEvaluationDataRow exampleRow = followerEvaluationExampleDataSet.getDataRows().get(i);
            FollowerEvaluationDataRow chromosomeRow = followerEvaluationChromosomeDataSet.getDataRows().get(i);

            // TODO exponent to settings
            double oldValue = fitness.get(0);
            double newValue = FitnessFunctions.distanceFitness(
                    exampleRow.getDistanceToTarget(),
                    chromosomeRow.getDistanceToTarget(),
                    2);

            fitness.set(0, oldValue + newValue);
        }
        return fitness;

    }
}
