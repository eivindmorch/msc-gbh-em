package training;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import logging.Logger;
import training.btree.task.IsCloseEnough;
import training.btree.task.IsApproaching;
import training.btree.task.Move;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Selector;
import data.ProcessedData;
import training.btree.task.Wait;
import util.Grapher;
import util.Reader;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static util.Values.*;

public class Trainer {

    private ProcessedData exampleData, iterationData;
    private double fitness;

    private void run() {
        Reader exampleDataReader = new Reader(exampleDataFilePath);
        Reader iterationDataReader = new Reader(iterationDataFilePath);

        List<String> exampleDataLine = exampleDataReader.readLine();
        List<String> iterationDataLine = iterationDataReader.readLine();

        while (exampleDataLine != null && iterationDataLine != null) {
            ProcessedData exampleData = new ProcessedData(exampleDataLine);
            ProcessedData iterationData = new ProcessedData(iterationDataLine);

            fitness += distanceFitness(exampleData.getDistance(), iterationData.getDistance());

            exampleDataLine = exampleDataReader.readLine();
            iterationDataLine = iterationDataReader.readLine();
            System.out.println(fitness);
        }
    }

    private double distanceFitness(double exampleDistance, double iterationDistance) {
        return Math.pow(Math.abs(exampleDistance - iterationDistance), 2);
    }

    private BehaviorTree cloneBehaviorTree(BehaviorTree btree) {
        Task root = btree.getChild(0);
        Task newRoot =  cloneSubtreeAndInsertChild(root, null, null, 0);
        return new BehaviorTree(newRoot);
    }

    private BehaviorTree cloneBehaviorTreeAndInsertChild(BehaviorTree btree, Task insertParent, Task insertChild, int insertIndex) {
        Task root = btree.getChild(0);
        Task newRoot =  cloneSubtreeAndInsertChild(root, insertParent, insertChild, insertIndex);
        return new BehaviorTree(newRoot);
    }

    private Task cloneSubtreeAndInsertChild(Task btree, Task insertParent, Task insertChild, int insertIndex) {
        Task newBtree = instantiateTaskObject(btree);
        if (insertIndex < 0 || (btree == insertParent && insertIndex > btree.getChildCount())) {
            throw new IllegalArgumentException("Invalid insertion index: " + insertIndex);
        }
        for (int i = 0; i < btree.getChildCount(); i++) {
            if (btree == insertParent && i == insertIndex) {
                newBtree.addChild(insertChild);
            }
            Task child = cloneSubtreeAndInsertChild(btree.getChild(i), insertParent, insertChild, insertIndex);
            newBtree.addChild(child);
        }
        if (btree == insertParent && btree.getChildCount() == insertIndex) {
            newBtree.addChild(insertChild);
        }
        return newBtree;
    }

    private Task instantiateTaskObject(Task task) {
        Task newTask = null;
        try {
            newTask = task.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return newTask;
    }

    public static void main(String[] args) {
        Trainer trainer = new Trainer();
//        trainer.behaviorTreeTest();
        trainer.simEngineTest();
    }

    private void behaviorTreeTest() {
        Selector selector1 = new Selector(new IsApproaching(), new IsCloseEnough());
        Sequence sequence1 = new Sequence(selector1, new Wait());
        Sequence sequence2 = new Sequence(sequence1, new Move());
        BehaviorTree btree = new BehaviorTree(sequence2);
        Grapher grapher = new Grapher("Original");
        grapher.graph(btree);

        Grapher grapher1 = new Grapher("Clone");
        grapher1.graph(cloneBehaviorTree(btree));

        Grapher grapher2 = new Grapher("Clone with insertion");
        grapher2.graph(cloneBehaviorTreeAndInsertChild(btree, sequence1, new Move(), 2));
    }

    private void simEngineTest() {
        Logger logger = new Logger();

        String vrfBin64 = System.getenv("MAK_VRFDIR64") + "/bin64/";
        String llbmlPluginPath = "LLBMLSimHLA1516e_VC10.dll";
        String fedFile = "RPR_FOM_v2.0_1516-2010.xml";
        String scenarioPath = "C:/MAK/vrforces4.5/userData/scenarios/it3903/follow_time-constrained-run-to-complete.scnx";

        String core = "cmd /c vrfSimHLA1516e.exe --appNumber 3001 --siteId 1 --timeManagement --execName rlo --fedFileName " + fedFile;
        String scenario = " --scenarioFileName " + scenarioPath;
        String fomModules = " --fomModules MAK-VRFExt-1_evolved.xml --fomModules MAK-DIGuy-2_evolved.xml --fomModules MAK-LgrControl-1_evolved.xml --fomModules MAK-VRFAggregate-1_evolved.xml --fomModules MAK-DynamicTerrain-1_evolved.xml --fomModules LLBML_v2_6.xml";
        String plugins = " --loadPlugin " + llbmlPluginPath; //TODO Test
        String cmd = core + scenario + fomModules + " --rprFomVersion 2.0" + plugins;
        System.out.println(cmd);

        // TODO Command works when executing manually, but not when using Runtime exec
        // TODO Log simengine output to file
        try {
            Process process = Runtime.getRuntime().exec(cmd, null, new File(vrfBin64));

            System.out.println();
            System.out.println("---------- STARTING SIMULATION ENGINE ----------");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (true) {
                System.out.println(bufferedReader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

////      TODO Can't find file
//        List<String> params = java.util.Arrays.asList(
//                "vrfSimHLA1516e.exe",
//                "-s 1",
//                "-a 3001",
//                "-x test",
//                "-F " + fedFile);
//        ProcessBuilder processBuilder = new ProcessBuilder(params);
//        processBuilder.directory(new File(vrfBin64));
//        try {
//            Process process = processBuilder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
