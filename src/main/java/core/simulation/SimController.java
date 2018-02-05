package core.simulation;


import hla.rti1516e.ObjectInstanceHandle;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.simulation.federate.Federate;
import core.simulation.federate.PhysicalEntityUpdatedListener;
import core.simulation.federate.TickListener;
import core.unit.UnitHandler;
import core.unit.UnitLogger;
import core.util.SystemStatus;

import static core.util.SystemUtil.sleepMilliseconds;


public class SimController implements TickListener, PhysicalEntityUpdatedListener {

    private static SimController instance;

    private final Logger logger = LoggerFactory.getLogger(SimController.class);

    private SimEngine simEngine;
    private SimGui simGui;

    private int ticksToPlay;
    private SimulationEndedListener simulationEndedListener;

    private int totalTicks;
    long startTime;

    private SimController() {}

    public static SimController getInstance() {
        if (instance == null) {
            instance = new SimController();
        }
        return instance;
    }

    @Override
    public void tick(double timestamp) {
        UnitHandler.updateUnits(timestamp);
        UnitLogger.logAllRegisteredUnits();
        UnitHandler.tickAllControlledUnits();

        // If ticksToPlay == 0, then run infinite number of ticks
        if (ticksToPlay > 0) {

            totalTicks++;
            if (totalTicks % 100 == 0) {
                logger.debug("Ticks: " + totalTicks +
                        " | Average time per tick: " + ((System.currentTimeMillis() - startTime) / 100) + "ms");
                startTime = System.currentTimeMillis();
            } else if (totalTicks == 1) {
                logger.debug("First tick");
            }

            ticksToPlay--;
            if (ticksToPlay == 0) {
                pause();
                simulationEndedListener.onSimulationEnd();
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

    public void play() {
        totalTicks = 0;
        logger.info("Playing scenario.");
        startTime = System.currentTimeMillis();
        Federate.getInstance().enableTimeAdvancement();
        Federate.getInstance().sendCgfPlayInteraction();
    }

    /**
     * Runs a specified number of ticks before pausing the simulation and notifying the listener.
     * @param numOfTicks the number of ticks to run
     * @param simulationEndedListener
     */
    public void play(int numOfTicks, SimulationEndedListener simulationEndedListener) {
        logger.info("Waiting for all units to be discovered.");
        while (Federate.getInstance().unitsDiscovered < 2) {
            sleepMilliseconds(500);
        }
        logger.info("All units to be discovered -> continuing.");
        sleepMilliseconds(250);
        ticksToPlay = numOfTicks;
        this.simulationEndedListener = simulationEndedListener;
        play();
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
        Federate.getInstance().holdTimeAdvancement();
        UnitHandler.reset();
        UnitLogger.reset();
        Federate.getInstance().sendCgfLoadScenarioInteraction(scenarioPath);
        SystemStatus.currentScenario = scenarioPath;
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
}
