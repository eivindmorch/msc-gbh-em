package core.training;

import core.data.DataSet;
import core.data.rows.DataRow;

import java.util.ArrayList;

public interface FitnessEvaluator {

    <D extends DataRow> ArrayList<Double> evaluate(DataSet<D> dataSet1, DataSet<D> dataSet2) throws Exception;
}
