package experiments.experiment1;

import core.data.DataSet;
import core.data.rows.DataRow;
import core.model.btree.BehaviorTreeUtil;
import core.training.Chromosome;
import core.training.FitnessEvaluator;
import core.training.FitnessFunctions;
import experiments.experiment1.data.rows.FollowerEvaluationDataRow;

import java.util.ArrayList;
import java.util.List;

public class Experiment1FitnessEvaluator implements FitnessEvaluator {

    private final double DISTANCE_FITNESS_EXPONENT;

    Experiment1FitnessEvaluator(double distanceFitnessExponent) {
        this.DISTANCE_FITNESS_EXPONENT = distanceFitnessExponent;
    }

    @Override
    public <D extends DataRow> ArrayList<Double> evaluate(
            Chromosome chromosome,
            List<DataSet<D>> exampleDataSets,
            List<DataSet<D>> chromosomeDataSets)
            throws Exception
    {

        if (exampleDataSets.size() != chromosomeDataSets.size()) {
            throw new Exception("Uneven number of example and chromosome data sets");
        }

        ArrayList<Double> fitness = new ArrayList<>();

        for (int i = 0; i < exampleDataSets.size(); i++) {
            DataSet<D> exampleDataSet = exampleDataSets.get(i);
            DataSet<D> chromosomeDataSet = chromosomeDataSets.get(i);
            fitness.add(evaluateDistanceFitness(exampleDataSet, chromosomeDataSet));
        }

        // Add tree size
        fitness.add((double) chromosome.getBtree().getSize());

        return fitness;
    }

    private <D extends DataRow> double evaluateDistanceFitness(DataSet<D> exampleDataSet, DataSet<D> chromosomeDataSet) throws Exception {
        if (exampleDataSet.getNumOfTicks() != chromosomeDataSet.getNumOfTicks()) {
            // TODO Log warning
        }
        if (!exampleDataSet.getDataSetName().equals(FollowerEvaluationDataRow.dataSetName)
                || !chromosomeDataSet.getDataSetName().equals(FollowerEvaluationDataRow.dataSetName)) {
            throw new Exception("Incompatible data sets: "
                    + exampleDataSet.getDataSetName()
                    + ", " + chromosomeDataSet.getDataSetName()
            );
        }

        DataSet<FollowerEvaluationDataRow> followerEvaluationExampleDataSet = (DataSet<FollowerEvaluationDataRow>) exampleDataSet;
        DataSet<FollowerEvaluationDataRow> followerEvaluationChromosomeDataSet = (DataSet<FollowerEvaluationDataRow>) chromosomeDataSet;

        double value = 0;

        int numOfTicks = Math.min(exampleDataSet.getNumOfTicks(), chromosomeDataSet.getNumOfTicks());
        for (int i = 0; i < numOfTicks; i++) {
            FollowerEvaluationDataRow exampleRow = followerEvaluationExampleDataSet.getDataRows().get(i);
            FollowerEvaluationDataRow chromosomeRow = followerEvaluationChromosomeDataSet.getDataRows().get(i);

            value += FitnessFunctions.distanceFitness(
                    exampleRow.getDistanceToTarget(),
                    chromosomeRow.getDistanceToTarget(),
                    DISTANCE_FITNESS_EXPONENT);
        }
        return value;
    }
}
