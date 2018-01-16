package core.settings;

import core.training.algorithms.Algorithm;
import core.training.algorithms.NSGA2;
import core.training.algorithms.SimpleSingleObjectiveGA;

public abstract class TrainingSettings {

    public static final String[] examples = new String[] {"0.csv"};

    public static final Class<? extends Algorithm> algorithm = SimpleSingleObjectiveGA.class;

    public static final int epochs = 10;

}
