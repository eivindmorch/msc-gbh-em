package moeaframework;

import core.data.DataSet;
import core.data.rows.DataRow;
import core.simulation.Rti;
import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.unit.UnitHandler;
import core.util.Graphing.Grapher;
import experiments.experiment1.data.rows.FollowerEvaluationDataRow;
import experiments.experiment1.unit.Experiment1AddUnitMethod;
import experiments.experiment1.unit.Experiment1UnitInfo;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.List;

import static core.SystemSettings.INTRA_RESOURCES_EXAMPLES_FOLDER_PATH;
import static core.util.SystemUtil.sleepSeconds;

public abstract class Main {

    public static void main(String[] args) {

        Experiment1UnitInfo.init();
        UnitHandler.setAddUnitMethod(new Experiment1AddUnitMethod());


        Rti.getInstance().start();

        sleepSeconds(5);
        Federate.getInstance().start();

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        SimController.getInstance().startSimEngine();
//        SimController.getInstance().startSimGui();
        sleepSeconds(10);


        String[] exampleFileNames = new String[]{
                "experiment1/brooklyn-simple.csv"
        };

        List<DataSet<FollowerEvaluationDataRow>> exampleDataSets =
                loadExampleDataSets(exampleFileNames, FollowerEvaluationDataRow.class);

        TestProblem testProblem = new TestProblem(exampleDataSets.get(0));

        NSGAII nsgaii = new NSGAII(
                testProblem,
                new NondominatedSortingPopulation(),
                null,
                null,
                new TestVariation(),
                new TestInitialization(20)
        );

        for (int i = 0; i < 2000; i++) {
            System.out.println("\nGENERATION " + i);
            nsgaii.step();

//            if (i % 10 == 0) {
//                System.out.println("\nRefreshing fitness values");
//                for (Solution solution : nsgaii.getPopulation()) {
//                    testProblem.evaluate(solution);
//                }
//            }

            Grapher.closeAllGraphs();

            Population population = new Population(nsgaii.getPopulation());
            population.sort(new ObjectiveComparator(0));
            Grapher.graph(population);

            System.out.println("\n\n--- Population ----------------------------");
            for (int j = 0; j < population.size(); j++) {
                System.out.println(population.get(j));
            }

            Population result = new Population(nsgaii.getResult());
            result.sort(new ObjectiveComparator(0));
            Grapher.graph(result);

            System.out.println("\n--- Result (non-dominated) ----------------");
            for (int j = 0; j < result.size(); j++) {
                System.out.println(result.get(j));
            }
        }

//        PESA2 pesa2 = new PESA2(
//                new TestProblem(exampleDataSets.get(0)),
//                new TestVariation(),
//                new TestInitialization(20),
//                0,
//                0
//        );
//
//        for (int i = 0; i < 200; i++) {
//            pesa2.step();
//
//            Grapher.closeAllGraphs();
//            Grapher.graph(pesa2.getPopulation());
//            Population population = pesa2.getPopulation();
//            for (int j = 0; j < population.size(); j++) {
//                System.out.println(population.get(j) + ": "
//                        + population.get(j).getObjective(0) + ", " + population.get(j).getObjective(1));
//            }
//        }

    }

    private static <D extends DataRow> ArrayList<DataSet<D>> loadExampleDataSets(
            String[] exampleFileNames, Class<D> evaluationDataRowClass)
    {
        ArrayList<DataSet<D>> exampleDataSets = new ArrayList<>();
        for (String exampleName : exampleFileNames) {
            exampleDataSets.add(new DataSet<>(evaluationDataRowClass, INTRA_RESOURCES_EXAMPLES_FOLDER_PATH + exampleName));
        }
        return exampleDataSets;
    }
}
