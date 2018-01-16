package core.training.algorithms;

import core.data.ExampleDataSet;
import core.data.rows.DataRow;
import core.training.Population;
import core.training.Trainer;


public abstract class Algorithm {

    Trainer trainer;
    Population population;

    Algorithm() {};

    Algorithm(Trainer trainer) {
        this.trainer = trainer;
    }

    public void setPopulation(Population population) {
        this.population = population;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public abstract void setup();

    public abstract void step(int epoch, int scenario, ExampleDataSet<? extends DataRow> exampleDataSet);

    public abstract void cleanup();

}
