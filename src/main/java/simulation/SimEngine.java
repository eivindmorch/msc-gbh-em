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
    private volatile boolean running;
    private Process process;

    // Todo make SimEngine able to load and reload scenario after init
    public SimEngine() {
    }

    public void init() {
        this.running = true;
        Thread thread = new Thread(this);
        thread.start();
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
            logger.info("Running simulation engine with command: " + cmd);
            process = Runtime.getRuntime().exec(cmd, null, new File(vrfBin64));

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Todo replace with thread
            while (running) {
                logger.info(bufferedReader.readLine());
            }
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void destroy() {
        this.running = false;
    }
}
