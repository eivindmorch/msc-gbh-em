package experiments.experiment1;

import core.data.DataSet;
import core.data.DataRow;
import core.training.Chromosome;
import core.training.FitnessEvaluator;
import core.util.Geometer;
import experiments.experiment1.data.rows.RawDataRow;

import java.util.LinkedHashMap;
import java.util.List;

public class WandererEvaluator implements FitnessEvaluator {

    private final double DISTANCE_FITNESS_EXPONENT;

    WandererEvaluator(double distanceFitnessExponent) {
        this.DISTANCE_FITNESS_EXPONENT = distanceFitnessExponent;
    }

    @Override
    public <D extends DataRow> LinkedHashMap<String, Double> evaluate(
            Chromosome chromosome,
            List<DataSet<D>> exampleDataSets,
            List<DataSet<D>> chromosomeDataSets)
            throws Exception
    {

        if (exampleDataSets.size() != chromosomeDataSets.size()) {
            throw new Exception("Uneven number of example and chromosome data sets");
        }

        LinkedHashMap<String, Double> fitness = new LinkedHashMap<>();

        for (int i = 0; i < exampleDataSets.size(); i++) {
            DataSet<D> exampleDataSet = exampleDataSets.get(i);
            DataSet<D> chromosomeDataSet = chromosomeDataSets.get(i);
            fitness.put("Scenario " + i, evaluateDistanceFitness(exampleDataSet, chromosomeDataSet));
        }

        // Add tree size
        fitness.put("Size", (double) chromosome.getBehaviourTreeRoot().getSize());
        return fitness;
    }

    private <D extends DataRow> double evaluateDistanceFitness(DataSet<D> exampleDataSet, DataSet<D> chromosomeDataSet) throws Exception {


        if (exampleDataSet.getNumOfTicks() != chromosomeDataSet.getNumOfTicks()) {
            // TODO Log warning
        }
        if (!exampleDataSet.getDataSetName().equals(RawDataRow.dataSetName)
                || !chromosomeDataSet.getDataSetName().equals(RawDataRow.dataSetName)) {
            throw new Exception("Incompatible data sets: "
                    + exampleDataSet.getDataSetName()
                    + ", " + chromosomeDataSet.getDataSetName()
            );
        }

        DataSet<RawDataRow> exampleWandererRawDataSet = (DataSet<RawDataRow>) exampleDataSet;
        DataSet<RawDataRow> chromosomeWandererRawDataSet = (DataSet<RawDataRow>) chromosomeDataSet;

        double value = 0;

        int numOfTicks = Math.min(exampleDataSet.getNumOfTicks(), chromosomeDataSet.getNumOfTicks());
        for (int i = 0; i < numOfTicks; i++) {
            RawDataRow exampleRow = exampleWandererRawDataSet.getDataRows().get(i);
            RawDataRow chromosomeRow = chromosomeWandererRawDataSet.getDataRows().get(i);

            value += Geometer.distance(exampleRow.getLla(), chromosomeRow.getLla());
            System.out.println(Geometer.distance(exampleRow.getLla(), chromosomeRow.getLla()));
//            value += Math.pow(Geometer.distance(exampleRow.getLla(), chromosomeRow.getLla()), DISTANCE_FITNESS_EXPONENT);
        }
        System.out.println(value + " - " + chromosomeDataSet.getNumOfTicks());
        return value;
    }
}
