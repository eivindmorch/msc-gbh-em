package core.training;

import core.data.DataSet;
import core.data.rows.DataRow;

import java.util.ArrayList;
import java.util.List;

public interface FitnessEvaluator {

    <D extends DataRow> ArrayList<Double> evaluate(
            Chromosome chromosome,
            List<DataSet<D>> exampleDataSets,
            List<DataSet<D>> chromosomeDataSets
    ) throws Exception;
}
