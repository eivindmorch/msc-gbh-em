package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Rti implements Runnable {

    private Logger logger = LoggerFactory.getLogger(Rti.class);
    private volatile boolean running;
    private Process process;

    public void init() {
        this.running = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        String rtiDir = System.getenv("MAK_RTIDIR") + "/bin/";
        String cmd = "rtiexec.exe";

        try {
            logger.info("Starting MAK RTI.");
            process = Runtime.getRuntime().exec(cmd, null, new File(rtiDir));

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

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
