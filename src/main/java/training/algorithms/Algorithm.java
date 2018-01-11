package training.algorithms;

import training.Population;
import training.Trainer;


public abstract class Algorithm {

    Trainer trainer;
    Population population;

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

    public abstract void epoch();

    public abstract void cleanup();

}
