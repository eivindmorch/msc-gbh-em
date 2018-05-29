package experiments;

import core.simulation.SimController;
import core.simulation.hla.HlaManager;
import core.unit.UnitHandler;

import static core.util.SystemUtil.sleepSeconds;

public abstract class ExperimentInitialiser {

    public static void setup(
            UnitTypeInfoInitialiser unitTypeInfoInitialiser,
            UnitHandler.AddUnitMethod addUnitMethod,
            boolean startRti,
            boolean startSimEngine,
            boolean startSimGui
    ) {

        unitTypeInfoInitialiser.initUnitTypeInfo();
        UnitHandler.setAddUnitMethod(addUnitMethod);

        if (startRti) {
            HlaManager.getInstance().startRti();
            sleepSeconds(10);
        }

        HlaManager.getInstance().connectFederate();

        HlaManager.getInstance().addTickListener(SimController.getInstance());
        HlaManager.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        if (startSimEngine) {
            SimController.getInstance().startSimEngine();
        }

        if (startSimGui) {
            SimController.getInstance().startSimGui();
        }

        sleepSeconds(10);
    }
}
