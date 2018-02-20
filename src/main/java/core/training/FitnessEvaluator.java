package core.training;

import core.data.DataSet;
import core.data.rows.DataRow;

import java.util.HashMap;
import java.util.List;

public interface FitnessEvaluator {

    <D extends DataRow> HashMap<String, Double> evaluate(
            Chromosome chromosome,
            List<DataSet<D>> exampleDataSets,
            List<DataSet<D>> chromosomeDataSets
    ) throws Exception;
}
