package core.training;

import core.data.DataSet;
import core.data.rows.DataRow;

import java.util.LinkedHashMap;
import java.util.List;

public interface FitnessEvaluator {

    <D extends DataRow> LinkedHashMap<String, Double> evaluate(
            Chromosome chromosome,
            List<DataSet<D>> exampleDataSets,
            List<DataSet<D>> chromosomeDataSets
    ) throws Exception;
}
