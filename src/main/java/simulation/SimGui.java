package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.SimSettings;
import util.ProcessLoggerThread;
import util.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class SimGui implements Runnable {

    private Logger logger = LoggerFactory.getLogger(SimGui.class);

    private CountDownLatch latch;

    public void start() {
        latch = new CountDownLatch(1);
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        String vrfBin64Dir = SimSettings.vrfDirectory + "/bin64/";
        String executable = vrfBin64Dir + "vrfGui.exe";

        // Options
        String protocol = "--hla1516e";
        String appNumber = "--appNumber " + (SimSettings.simGUIApplicationNumber);
        String siteId = "--siteId " + SimSettings.siteId;
        String execName = "--execName " + SimSettings.federationName;
        String fedFileName = "--fedFileName " + SimSettings.federationFile;
        String rprFomVersion = "--rprFomVersion " + SimSettings.rprFomVersion;

        ArrayList<String> fomModules = new ArrayList<>(SimSettings.fomModules.length);
        for (int i = 0; i < SimSettings.fomModules.length; i++) {
            fomModules.add("--fomModules " + SimSettings.fomModules[i]);
        }

        ArrayList<String> processParams = new ArrayList<>();
        processParams.addAll(Arrays.asList(executable, protocol, appNumber, siteId, execName, fedFileName));
        processParams.addAll(fomModules);
        processParams.add(rprFomVersion);

        try {
            logger.info("Running simulation GUI with parameters: " + SystemUtil.commandOptionsListToString(processParams));
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

    public void destroy() {
        // TODO Does not destroy subprocesses
        logger.info("Destroying simulation GUI.");
        latch.countDown();
    }

}