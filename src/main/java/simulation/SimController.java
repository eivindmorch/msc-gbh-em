package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.federate.Federate;
import simulation.federate.PhysicalEntityUpdatedListener;
import simulation.federate.TickListener;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import unit.*;

import java.util.concurrent.TimeUnit;

public class SimController implements TickListener, PhysicalEntityUpdatedListener {

    private final Logger logger = LoggerFactory.getLogger(SimController.class);


    public void init() {
        Federate federate = new Federate();
        federate.init();
        federate.addTickListener(this);
        federate.addPhysicalEntityUpdatedListener(this);
    }

    @Override
    public void tick(double timestamp) {
        UnitHandler.updateUnits(timestamp);
        UnitLogger.logAllRegisteredUnits();
        UnitHandler.tickAllControlledUnits();

        delay(200);
    }

    private void delay(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void physicalEntityUpdated(PhysicalEntityObject physicalEntity) {
        UnitHandler.addUnit(physicalEntity);
    }

    public void initSimEngine() {
        SimEngine simEngine = new SimEngine();
        simEngine.init();
    }
}
