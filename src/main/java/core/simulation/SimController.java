package core.simulation;


import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.simulation.federate.Federate;
import core.simulation.federate.PhysicalEntityUpdatedListener;
import core.simulation.federate.TickListener;
import core.unit.UnitHandler;
import core.unit.UnitLogger;
import core.util.SystemStatus;


public class SimController implements TickListener, PhysicalEntityUpdatedListener {

    private static SimController instance;

    private final Logger logger = LoggerFactory.getLogger(SimController.class);

    private SimEngine simEngine;
    private SimGui simGui;

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
//        SystemUtil.sleepMilliseconds(SimSettings.simulationTickDelayInMilliseconds);
    }

    @Override
    public void physicalEntityUpdated(PhysicalEntityObject physicalEntity) {
        UnitHandler.addUnit(physicalEntity);
    }

    public void play() {
        logger.info("Playing scenario.");
        Federate.getInstance().sendCgfPlayInteraction();
    }

    public void pause() {
        logger.info("Pausing scenario.");
        Federate.getInstance().sendCgfPauseInteraction();
    }

    public void rewind() {
        logger.info("Rewinding scenario.");
        Federate.getInstance().sendCgfPauseInteraction();
        UnitHandler.reset();
        UnitLogger.reset();
        // TODO Delay here? Ask Rikke/Martin
        Federate.getInstance().sendCgfRewindInteraction();
    }

    public void loadScenario(String scenarioPath) {
        logger.info("Loading scenario " + scenarioPath + ".");
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
