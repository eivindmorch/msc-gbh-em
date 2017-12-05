package util;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ProcessLoggerThread implements Runnable {

    private Process process;
    private Logger logger;
    private BufferedReader reader;

    public ProcessLoggerThread(Process process, Logger logger) {
        this.process = process;
        this.logger = logger;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader( new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
