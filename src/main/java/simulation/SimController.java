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
        SystemUtil.sleepMilliseconds(SimSettings.simulationTickDelayInMilliseconds);
    }

    @Override
    public void physicalEntityUpdated(PhysicalEntityObject physicalEntity) {
        UnitHandler.addUnit(physicalEntity);
    }

    public void play() {
        Federate.getInstance().sendCgfPlayInteraction();
    }

    public void pause() {
        Federate.getInstance().sendCgfPauseInteraction();
    }

    public void rewind() {
        UnitHandler.reset();
        UnitLogger.reset();
        Federate.getInstance().sendCgfPauseInteraction();
        // TODO Delay here? Ask Rikke/Martin
        Federate.getInstance().sendCgfRewindInteraction();
    }

    public void loadScenario(String scenarioPath) {
        UnitHandler.reset();
        UnitLogger.reset();
        Federate.getInstance().sendCgfLoadScenarioInteraction(scenarioPath);
        SystemStatus.currentScenario = FilenameUtils.getBaseName(scenarioPath);
    }

    public void startSimEngine() {
        simEngine = new SimEngine();
        simEngine.start();
    }

    public void startSimGui() {
        simGui = new SimGui();
        simGui.start();
    }
}
