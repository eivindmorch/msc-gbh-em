package simulation;


import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.SimSettings;
import simulation.federate.Federate;
import simulation.federate.PhysicalEntityUpdatedListener;
import simulation.federate.TickListener;
import unit.UnitHandler;
import unit.UnitLogger;
import util.SystemStatus;
import util.SystemUtil;


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
        UnitHandler.reset();
        UnitLogger.reset();
        Federate.getInstance().sendCgfPauseInteraction();
        // TODO Delay here? Ask Rikke/Martin
        Federate.getInstance().sendCgfRewindInteraction();
    }

    public void loadScenario(String scenarioPath) {
        logger.info("Loading scenario " + scenarioPath + ".");
        UnitHandler.reset();
        UnitLogger.reset();
        Federate.getInstance().sendCgfLoadScenarioInteraction(scenarioPath);
        SystemStatus.currentScenario = FilenameUtils.getBaseName(scenarioPath);
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
