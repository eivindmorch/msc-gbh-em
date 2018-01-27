package moeaframework;

import com.badlogic.gdx.ai.btree.Task;
import core.data.DataSet;
import core.model.btree.BehaviorTreeUtil;
import core.simulation.SimController;
import core.simulation.SimulationEndedListener;
import core.unit.ControlledUnit;
import core.util.SystemUtil;
import experiments.experiment1.Experiment1FitnessEvaluator;
import experiments.experiment1.data.rows.FollowerEvaluationDataRow;
import experiments.experiment1.unit.FollowerUnit;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static core.util.SystemUtil.sleepSeconds;

public class TestProblem implements Problem, SimulationEndedListener{

    private DataSet<FollowerEvaluationDataRow> exampleDataSet;

    private volatile boolean simulationRunning;
    private final Object SIMULATION_ENDED_LOCK = new Object();

    public TestProblem(DataSet<FollowerEvaluationDataRow> exampleDataSet) {
        this.exampleDataSet = exampleDataSet;
    }

    @Override
    public String getName() {
        return "Experiment1 - Follow";
    }

    @Override
    public int getNumberOfVariables() {
        return 0;
    }

    @Override
    public int getNumberOfObjectives() {
        return 2;
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public void evaluate(Solution solution) {
        // TODO Multiple datasets
        TestSolution testSolution = (TestSolution) solution;
        Task btreeRoot = testSolution.getBtreeRoot();

        if (!exampleDataSet.getScenarioPath().equals(SimController.getInstance().getCurrentScenario())) {
            SimController.getInstance().loadScenario(exampleDataSet.getScenarioPath());
            sleepSeconds(5);
        } else {
            SimController.getInstance().rewind();
        }

        ControlledUnit.setControlledUnitBtreeMap(FollowerUnit.class, btreeRoot);
        runSimulationForNTicks(exampleDataSet.getNumOfTicks());
        setFitness(testSolution);
    }

    private void runSimulationForNTicks(int ticks){
        simulationRunning = true;
        SimController.getInstance().play(ticks, this);
        synchronized (SIMULATION_ENDED_LOCK) {
            while(simulationRunning) {
                try {
                    SIMULATION_ENDED_LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSimulationEnd() {
        simulationRunning = false;
        synchronized (SIMULATION_ENDED_LOCK) {
            SIMULATION_ENDED_LOCK.notifyAll();
        }
    }

    private void setFitness(TestSolution solution) {
        System.out.print(".");

        String intraResourcesScenarioLogsPath = SystemUtil.getDataFileIntraResourcesFolderPath(0, 0, 0);
        DataSet<FollowerEvaluationDataRow> chromosomeDataSet = new DataSet<>(
                FollowerEvaluationDataRow.class,
                intraResourcesScenarioLogsPath
                        + exampleDataSet.getUnitMarking()
                        + "/" + exampleDataSet.getDataSetName()
                        + ".csv"
        );
        Experiment1FitnessEvaluator experiment1FitnessEvaluator = new Experiment1FitnessEvaluator();
        ArrayList<Double> equalityFitnessValues = null;
        try {
            equalityFitnessValues = experiment1FitnessEvaluator.evaluate(exampleDataSet, chromosomeDataSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        solution.setObjective(0, equalityFitnessValues.get(0));

        int size = BehaviorTreeUtil.getSize(solution.getBtreeRoot());
        solution.setObjective(1, size);
    }


    @Override
    public Solution newSolution() {
        try {
            return new TestSolution(0, 2, BehaviorTreeUtil.generateRandomTree(FollowerUnit.class));
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {

    }
}
