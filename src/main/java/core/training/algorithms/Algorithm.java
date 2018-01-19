package core.training.algorithms;

import core.data.DataSet;
import core.data.rows.DataRow;
import core.training.FitnessEvaluator;
import core.training.Population;
import core.training.Trainer;


public abstract class Algorithm<D extends DataRow> {

    Trainer trainer;
    Population population;
    FitnessEvaluator fitnessEvaluator;
    public Class<D> evaluationDataRowClass;

    public Algorithm(Class<D> evaluationDataRowClass, FitnessEvaluator fitnessEvaluator) {
        this.population = new Population();
        this.fitnessEvaluator = fitnessEvaluator;
        this.evaluationDataRowClass = evaluationDataRowClass;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public abstract void setup();

    public abstract void step(int epoch, int exampleNumber, DataSet<D> exampleDataSet);

    public abstract void cleanup();

}
