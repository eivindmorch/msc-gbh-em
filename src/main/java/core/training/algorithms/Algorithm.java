package core.training.algorithms;

import core.data.DataSet;
import core.data.rows.DataRow;
import core.training.Chromosome;
import core.training.FitnessEvaluator;
import core.training.Population;
import core.training.Trainer;


public abstract class Algorithm<D extends DataRow, C extends Chromosome> {

    protected Trainer trainer;
    protected Population<C> population;
    protected FitnessEvaluator fitnessEvaluator;
    public Class<D> evaluationDataRowClass;

    public Algorithm(Class<D> evaluationDataRowClass, FitnessEvaluator fitnessEvaluator) {
        this.population = new Population<>();
        this.fitnessEvaluator = fitnessEvaluator;
        this.evaluationDataRowClass = evaluationDataRowClass;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Population<C> getPopulation() {
        return population;
    }

    public abstract void setup();

    public abstract void step(int epoch, int exampleNumber, DataSet<D> exampleDataSet);

    public abstract void cleanup();

}
