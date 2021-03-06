package core.training.algorithms;

import core.data.DataSet;
import core.data.DataRow;
import core.training.Chromosome;
import core.training.Population;
import core.training.Trainer;

import java.util.List;


public abstract class Algorithm<D extends DataRow, C extends Chromosome> {

    protected Trainer<?, D> trainer;
    protected Population<C> population;

    public void setTrainer(Trainer<?, D> trainer) {
        this.trainer = trainer;
    }

    public Population<C> getPopulation() {
        return population;
    }

    public abstract void setup();

    @SuppressWarnings("unchecked")
    public abstract void step(int epoch, List<DataSet<D>> exampleDataSets);
}
