package core.simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.settings.SimSettings;
import core.util.ProcessLoggerThread;
import core.util.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class SimEngine implements Runnable {

    private Logger logger = LoggerFactory.getLogger(SimEngine.class);

    private CountDownLatch latch;

     public void start() {
         latch = new CountDownLatch(1);
         Thread thread = new Thread(this);
         thread.start();
    }

    @Override
    public void run() {
        String vrfBin64Dir = SimSettings.vrfDirectory + "/bin64/";
        String executable = vrfBin64Dir + "vrfSimHLA1516e.exe";

        // Options
        String appNumber = "--appNumber " + SimSettings.simEngineApplicationNumber;
        String siteId = "--siteId " + SimSettings.siteId;
        String execName = "--execName " + SimSettings.federationName;
        String fedFileName = "--fedFileName " + SimSettings.federationFile;
        String rprFomVersion = "--rprFomVersion " + SimSettings.rprFomVersion;
        String timeManagement = SimSettings.useTimeManagement ? "--timeManagement" :  "";
        String startInRunMode = SimSettings.startInRunMode ? "--startInRunMode" : "";

        ArrayList<String> plugins = new ArrayList<>(SimSettings.plugins.length);
        for (int i = 0; i < SimSettings.plugins.length; i++) {
            plugins.add("--loadPlugin " + SimSettings.plugins[i]);
        }
        ArrayList<String> fomModules = new ArrayList<>(SimSettings.fomModules.length);
        for (int i = 0; i < SimSettings.fomModules.length; i++) {
            fomModules.add("--fomModules " + SimSettings.fomModules[i]);
        }

        ArrayList<String> processParams = new ArrayList<>();
        processParams.addAll(Arrays.asList(executable, appNumber, siteId, timeManagement, execName, fedFileName));
        processParams.addAll(fomModules);
        processParams.add(rprFomVersion);
        processParams.addAll(plugins);
        processParams.add(startInRunMode);

        try {
            logger.info("Running simulation engine with parameters: " + SystemUtil.commandOptionsListToString(processParams));
            ProcessBuilder processBuilder = new ProcessBuilder(processParams);
            processBuilder.directory(new File(vrfBin64Dir));
            Process process = processBuilder.start();

            ProcessLoggerThread processLoggerThread = new ProcessLoggerThread(process, logger);
            processLoggerThread.start();

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processLoggerThread.destroy();
            process.destroy();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO Ask Martin if we can extends CgfControl with Destroy command
    public void destroy() {
        // TODO Does not destroy subprocesses
        logger.info("Destroying simulation engine.");
        latch.countDown();
    }

}