package core.settings;

import core.training.algorithms.Algorithm;
import core.training.algorithms.NSGA2;

public abstract class TrainingSettings {

    public static final String[] examples = new String[] {"2", "4"};

    public static final Class<? extends Algorithm> algorithm = NSGA2.class;

    public static final int epochs = 10;

}
