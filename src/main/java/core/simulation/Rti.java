package core.simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.util.ProcessLoggerThread;
import core.util.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static core.settings.SimSettings.rtiDirectory;

public class Rti implements Runnable {

    private static Rti instance;

    private Logger logger = LoggerFactory.getLogger(Rti.class);
    private CountDownLatch latch;

    private Rti() {}

    public static Rti getInstance() {
        if (instance == null) {
            instance = new Rti();
        }
        return instance;
    }

    public void start() {
        latch = new CountDownLatch(1);
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        String rtiBinDir = rtiDirectory + "/bin/";
        String executable = "rtiexec.exe";

        ArrayList<String> processParams = new ArrayList<>();
        processParams.add(executable);

        try {
            logger.info("Running MAK RTI with parameters: " + SystemUtil.commandOptionsListToString(processParams));
            ProcessBuilder processBuilder = new ProcessBuilder(processParams);
            processBuilder.directory(new File(rtiBinDir));
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
        // TODO Does not destroy subprocesses (RTIForwarder)
        logger.info("Destroying Rti.");
        latch.countDown();
    }
}