package core.simulation;


import core.util.ProcessLoggerThread;
import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.simulation.federate.Federate;
import core.simulation.federate.PhysicalEntityUpdatedListener;
import core.simulation.federate.TickListener;
import core.unit.UnitHandler;
import core.unit.UnitLogger;

import java.util.concurrent.locks.ReentrantLock;

import static core.util.SystemUtil.sleepMilliseconds;


public class SimController implements TickListener, PhysicalEntityUpdatedListener, ProcessLoggerThread.LineListener {

    private static SimController instance;

    private final Logger logger = LoggerFactory.getLogger(SimController.class);

    private SimEngine simEngine;
    private SimGui simGui;

    private String currentScenario;
    private int ticksToPlay;

    private int totalTicks;
    private long lastPlayTimestamp;
    private long lastNTicksTimestamp;

    public volatile boolean simulationRunning;
    public final ReentrantLock SIMULATION_RUNNING_LOCK = new ReentrantLock();

    public volatile boolean scenarioLoading;
    public final ReentrantLock SCENARIO_LOADING_LOCK = new ReentrantLock();

    private SimController() {}

    public static SimController getInstance() {
        if (instance == null) {
            instance = new SimController();
        }
        return instance;
    }

    public String getCurrentScenario() {
        return currentScenario;
    }

    @Override
    public void tick(double timestamp) {
        UnitHandler.updateUnits(timestamp);
        UnitLogger.logAllRegisteredUnits();
        UnitHandler.tickAllControlledUnits();

        // TODO Move to separate method
        // If ticksToPlay == 0, then run infinite number of ticks
        if (ticksToPlay > 0) {

            totalTicks++;
            if (totalTicks % SimSettings.numberOfTicksToCalculateAverageTickTime == 0) {
                logger.debug("Ticks: " + String.format("%4d", totalTicks)
                        + " | Average time per tick: " +
                        ((System.currentTimeMillis() - lastNTicksTimestamp) / SimSettings.numberOfTicksToCalculateAverageTickTime) + "ms");
                lastNTicksTimestamp = System.currentTimeMillis();
            } else if (totalTicks == 1) {
                lastNTicksTimestamp = lastPlayTimestamp;
                logger.debug("First tick");
            }

            ticksToPlay--;
            if (ticksToPlay == 0) {
                pause();
                onSimulationEnd();
            }
        }
    }

    @Override
    public void physicalEntityUpdated(PhysicalEntityObject physicalEntity) {
        UnitHandler.addUnit(physicalEntity);
    }

    @Override
    public void physicalEntityRemoved(ObjectInstanceHandle objectInstanceHandle) {
        UnitHandler.removeUnit(objectInstanceHandle);
    }

    /**
     * Runs a specified number of ticks before pausing the simulation and notifying the listener.
     * @param numOfTicks the number of ticks to run
     */
    public void play(int numOfTicks) {
        ticksToPlay = numOfTicks;
        play();
    }

    public void play() {
        simulationRunning = true;

//        waitForScenarioToSuccessfullyLoad();
        waitForAllUnitsToBeDiscovered(2);
        sleepMilliseconds(1000);
        waitForAllUnitsToBeUpdated();
        sleepMilliseconds(500);

        totalTicks = 0;
        logger.info("Playing scenario for " + ((ticksToPlay != 0) ? ticksToPlay : "unlimited") + " ticks.");
        lastPlayTimestamp = System.currentTimeMillis();
        Federate.getInstance().enableTimeAdvancement();
        Federate.getInstance().sendCgfPlayInteraction();
    }

//    private void waitForScenarioToSuccessfullyLoad(int secondsToWaitForSuccessfulLoad) {
//        synchronized (SCENARIO_LOADING_LOCK) {
//            double initialWaitingForScenarioToLoadTime = System.currentTimeMillis();
//            while(scenarioLoading) {
//                logger.info("Waiting for scenario to be loaded.");
//                try {
//                    SCENARIO_LOADING_LOCK.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (System.currentTimeMillis() - initialWaitingForScenarioToLoadTime >= secondsToWaitForSuccessfulLoad * 1000) {
//                    logger.info("Scenario not successfully loaded after " + secondsToWaitForSuccessfulLoad + " seconds.");
//                    retryLoadScenario();
//                    initialWaitingForScenarioToLoadTime = System.currentTimeMillis();
//                }
//            }
//        }
//        logger.info("Scenario successfully loaded -> continuing.");
//        sleepMilliseconds(500);
//    }

    // TODO Include number of units in example file
    private void waitForAllUnitsToBeDiscovered(int numOfUnits) {
        logger.info("START Waiting for all (" + numOfUnits + ") units to be discovered.");

        long timeoutIntervalStartTime = System.currentTimeMillis();

        while (Federate.getInstance().unitsDiscovered < numOfUnits) {
            sleepMilliseconds(500);
            if (System.currentTimeMillis() - timeoutIntervalStartTime > SimSettings.secondsToWaitForUnitsBeforeReload * 1000) {
                timeoutIntervalStartTime = System.currentTimeMillis();
                logger.warn("Not all units discovered. Reloading scenario.");
                loadScenario(currentScenario);
            }
        }
        logger.info("DONE All units discovered.");
    }

    // TODO Lock instead of sleep
    private void waitForAllUnitsToBeUpdated() {
        logger.info("START Waiting for all (" + Federate.getInstance().unitsDiscovered + ") units to be updated with values. Requesting queued messages from RTI.");
        while (UnitHandler.getNumOfUnits() < Federate.getInstance().unitsDiscovered) {
            Federate.getInstance().requestFlushQueue();
            sleepMilliseconds(1000);
        }
        logger.info("DONE All units updated with values.");
    }

    public void pause() {
        logger.info("Pausing scenario.");
        Federate.getInstance().sendCgfPauseInteraction();
        Federate.getInstance().holdTimeAdvancement();
    }

    public void rewind() {
        logger.info("Rewinding scenario.");
        Federate.getInstance().sendCgfRewindInteraction();
        UnitHandler.reset();
        UnitLogger.reset();
    }

    public void loadScenario(String scenarioPath) {
        logger.info("Loading scenario " + scenarioPath + ".");

//        scenarioLoading = true;
//        simEngine.getProcessLoggerThread().registerLineListener(
//                SimEngine.SUCCESSFULLY_LOADED_SCENARIO_OUTPUT_LINE,
//                this
//        );

        Federate.getInstance().holdTimeAdvancement();
        UnitHandler.reset();
        UnitLogger.reset();
        Federate.getInstance().sendCgfLoadScenarioInteraction(scenarioPath);
        currentScenario = scenarioPath;
    }

    private void retryLoadScenario() {
        logger.info("Attempting new loading of scenario " + currentScenario + ".");
        Federate.getInstance().sendCgfLoadScenarioInteraction(currentScenario);
    }

    public void startSimEngine() {
        logger.info("Starting SimEngine.");
        simEngine = new SimEngine();
        simEngine.start();
    }

    public void startSimGui() {
        logger.info("Starting SimGui.");
        simGui = new SimGui();
        simGui.start();
    }

    private void onSimulationEnd() {
        simulationRunning = false;
        synchronized (SIMULATION_RUNNING_LOCK) {
            SIMULATION_RUNNING_LOCK.notifyAll();
        }
    }

    @Override
    public void onLineOutput(String line) {
        if (line.equals(SimEngine.SUCCESSFULLY_LOADED_SCENARIO_OUTPUT_LINE)) {
            scenarioLoading = false;
            synchronized (SCENARIO_LOADING_LOCK) {
                SCENARIO_LOADING_LOCK.notifyAll();
            }
        }
    }
}
