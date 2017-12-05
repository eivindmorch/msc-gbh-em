package simulation;


import no.ffi.hlalib.datatypes.enumeratedData.StopFreezeReasonEnum8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.SimSettings;
import simulation.federate.Federate;
import simulation.federate.PhysicalEntityUpdatedListener;
import simulation.federate.TickListener;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import unit.*;
import util.SystemUtil;


public class SimController implements TickListener, PhysicalEntityUpdatedListener {

    private final Logger logger = LoggerFactory.getLogger(SimController.class);

    public void resume() {
        Federate.getInstance().sendStartResumeInteraction();
    }

    public void freeze() {
        Federate.getInstance().sendStopFreezeInteraction(StopFreezeReasonEnum8.Recess);
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

    public void startSimEngine() {
        SimEngine simEngine = new SimEngine();
        simEngine.start();
    }

    public void reset() {
        UnitHandler.reset();
        UnitLogger.reset();
    }
}
