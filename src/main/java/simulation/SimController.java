package simulation;


import no.ffi.hlalib.datatypes.enumeratedData.RPRboolean;
import no.ffi.hlalib.datatypes.enumeratedData.StopFreezeReasonEnum8;
import no.ffi.hlalib.datatypes.fixedRecordData.ClockTimeStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.EntityIdentifierStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.FederateIdentifierStruct;
import no.ffi.hlalib.interactions.HLAinteractionRoot.StartResumeInteraction;
import no.ffi.hlalib.interactions.HLAinteractionRoot.StopFreezeInteraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.federate.PhysicalEntityUpdatedListener;
import simulation.federate.TickListener;
import no.ffi.hlalib.objects.HLAobjectRoot.BaseEntity.PhysicalEntityObject;
import unit.*;


import java.util.concurrent.TimeUnit;

public class SimController implements TickListener, PhysicalEntityUpdatedListener {

    private final Logger logger = LoggerFactory.getLogger(SimController.class);


    public void init() {
    }


    // TODO Vr-forces does rewinding of scenario through DataInteraction (most likely)
    public void startScenario() {
        System.out.println("Sending StartResumeInteraction");
        StartResumeInteraction startResumeInteraction = new StartResumeInteraction();

        EntityIdentifierStruct receivingEntity = new EntityIdentifierStruct();
        receivingEntity.setFederateIdentifier(new FederateIdentifierStruct(0xFFFF, 0xFFFF));
        startResumeInteraction.setReceivingEntity(receivingEntity);

        System.out.println();
        System.out.println("SENDING StartResumeInteraction with following data:");
        System.out.println("Receiving entity: " + startResumeInteraction.getReceivingEntity().getFederateIdentifier());

        // TODO Can reset timestamp? -> interaction.setSimulationTime(); No, used for setting when to start again (i think)
        startResumeInteraction.sendInteraction();
    }

    public void stopScenario() {
        System.out.println("Sending StopFreezeInteraction");
        StopFreezeInteraction stopFreezeInteraction = new StopFreezeInteraction();

        EntityIdentifierStruct receivingEntity = new EntityIdentifierStruct();
        receivingEntity.setFederateIdentifier(new FederateIdentifierStruct(65535, 65535));
        stopFreezeInteraction.setReceivingEntity(receivingEntity);
        stopFreezeInteraction.setReason(StopFreezeReasonEnum8.StopForRestart);

        System.out.println();
        System.out.println("SENDING StopFreezeInteraction with following data:");
        System.out.println("Receiving entity: " + stopFreezeInteraction.getReceivingEntity().getFederateIdentifier());

        stopFreezeInteraction.sendInteraction();
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

    public void reset() {
        UnitHandler.reset();
        UnitLogger.reset();
    }
}
