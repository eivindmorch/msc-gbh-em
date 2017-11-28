package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unit.UnitHandler;
import unit.UnitLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimEngine implements Runnable {

    private Logger logger = LoggerFactory.getLogger(SimEngine.class);

    // Todo make SimEngine able to load and reload scenario after init
    public SimEngine() {
    }

    public void init() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        String vrfBin64 = System.getenv("MAK_VRFDIR64") + "/bin64/";
        String fedFile = "RPR_FOM_v2.0_1516-2010.xml";
        String scenarioPath = "C:/MAK/vrforces4.5/userData/scenarios/it3903/follow_time-constrained-run-to-complete.scnx";

        String core = "cmd /c vrfSimHLA1516e.exe --appNumber 3001 --siteId 1 --timeManagement --execName rlo --fedFileName " + fedFile;
        String scenario = " --scenarioFileName " + scenarioPath;
        String fomModules = " --fomModules MAK-VRFExt-1_evolved.xml --fomModules MAK-DIGuy-2_evolved.xml --fomModules MAK-LgrControl-1_evolved.xml --fomModules MAK-VRFAggregate-1_evolved.xml --fomModules MAK-DynamicTerrain-1_evolved.xml --fomModules LLBML_v2_6.xml";
        String plugins = " --loadPlugin LLBMLSimHLA1516e_VC10.dll";
        String rprVersion = " --rprFomVersion 2.0";
        String cmd = core + scenario + fomModules + rprVersion + plugins;

        try {
            logger.info("Running simEngine with command: " + cmd);
            Process process = Runtime.getRuntime().exec(cmd, null, new File(vrfBin64));

            logger.info("Starting simulation engine");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Todo replace with thread
            while (true) {
                logger.info(bufferedReader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        UnitHandler.reset();
        UnitLogger.reset();
        // TODO
        // Reset federation timestamp to 0
        // Reset scenario
    }
}
