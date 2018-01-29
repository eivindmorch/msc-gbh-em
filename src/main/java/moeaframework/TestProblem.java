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
import java.util.List;

import static core.util.SystemUtil.sleepSeconds;

public class TestProblem implements Problem, SimulationEndedListener{

    private List<DataSet<FollowerEvaluationDataRow>> exampleDataSets;

    private volatile boolean simulationRunning;
    private final Object SIMULATION_ENDED_LOCK = new Object();

    private ArrayList<TestSolution> evaluateQueue = new ArrayList<>();

    public TestProblem(List<DataSet<FollowerEvaluationDataRow>> exampleDataSets) {
        this.exampleDataSets = exampleDataSets;
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
        return exampleDataSets.size() + 1;
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public void evaluate(Solution solution) {
        evaluateQueue.add((TestSolution) solution);
        if (evaluateQueue.size() == 20) {
            evaluateQueue();
            evaluateQueue = new ArrayList<>();
        }
    }

    public void evaluateQueue() {

        for (int i = 0; i < exampleDataSets.size(); i++) {

            for (TestSolution testSolution : evaluateQueue) {
                System.out.println("Evaluating " + testSolution.getClass().getSimpleName() + "@" + testSolution.hashCode());

                Task btreeRoot = testSolution.getBtreeRoot();
                ControlledUnit.setControlledUnitBtreeMap(FollowerUnit.class, btreeRoot);


                System.out.println("\t" + exampleDataSets.get(i).getScenarioPath());

                DataSet<FollowerEvaluationDataRow> exampleDataSet = exampleDataSets.get(i);

                if (!exampleDataSet.getScenarioPath().equals(SimController.getInstance().getCurrentScenario())) {
                    SimController.getInstance().loadScenario(exampleDataSet.getScenarioPath());
                    sleepSeconds(5);
                } else {
                    SimController.getInstance().rewind();
                }

                runSimulationForNTicks(exampleDataSet.getNumOfTicks());
                setFitness(testSolution, i, exampleDataSet);
            }
        }
        for (TestSolution testSolution : evaluateQueue) {
            int size = BehaviorTreeUtil.getSize(testSolution.getBtreeRoot());
            testSolution.setObjective(exampleDataSets.size(), size);
        }

    }


//    @Override
//    public void evaluate(Solution solution) {
//
//        // TODO Add to list for evaluate queue, evaluate all at same time after nsgaii.step();
//
//        System.out.println("Evaluating " + solution.getClass().getSimpleName() + "@" + solution.hashCode());
//
//        TestSolution testSolution = (TestSolution) solution;
//        Task btreeRoot = testSolution.getBtreeRoot();
//
//        ControlledUnit.setControlledUnitBtreeMap(FollowerUnit.class, btreeRoot);
//
//        for (int i = 0; i < exampleDataSets.size(); i++) {
//
//            System.out.println("\t" + exampleDataSets.get(i).getScenarioPath());
//
//            DataSet<FollowerEvaluationDataRow> exampleDataSet = exampleDataSets.get(i);
//
//            if (!exampleDataSet.getScenarioPath().equals(SimController.getInstance().getCurrentScenario())) {
//                SimController.getInstance().loadScenario(exampleDataSet.getScenarioPath());
//                sleepSeconds(2);
//            } else {
//                SimController.getInstance().rewind();
//            }
//
//            runSimulationForNTicks(exampleDataSet.getNumOfTicks());
//            setFitness(testSolution, i, exampleDataSet);
//        }
//        int size = BehaviorTreeUtil.getSize(testSolution.getBtreeRoot());
//        testSolution.setObjective(exampleDataSets.size(), size);
//    }

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

    private void setFitness(TestSolution solution, int objectiveIndex, DataSet<FollowerEvaluationDataRow> exampleDataSet) {
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
        solution.setObjective(objectiveIndex, equalityFitnessValues.get(0));
    }


    @Override
    public Solution newSolution() {
        try {
            return new TestSolution(0, exampleDataSets.size() + 1, BehaviorTreeUtil.generateRandomTree(FollowerUnit.class));
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {

    }
}
