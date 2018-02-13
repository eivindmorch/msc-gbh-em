package core.util;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class ProcessLoggerThread implements Runnable {

    private Process process;
    private Logger logger;
    private BufferedReader reader;

    private Map<String, List<LineListener>> lineListenersMap;

    public ProcessLoggerThread(Process process, Logger logger) {
        this.process = process;
        this.logger = logger;
        this.lineListenersMap = Collections.synchronizedMap(new HashMap<>());
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void registerLineListener(String lineToListenFor, LineListener lineListener) {
        if (!lineListenersMap.containsKey(lineToListenFor)) {
            lineListenersMap.put(lineToListenFor, Collections.synchronizedList(new ArrayList<>()));
        }
        lineListenersMap.get(lineToListenFor).add(lineListener);
    }

    private void notifyPotentialLineListeners(String line) {
        if (lineListenersMap.containsKey(line)) {
            for (LineListener lineListener : lineListenersMap.get(line)) {
                lineListener.onLineOutput(line);
            }
            lineListenersMap.replace(line, new ArrayList<>());
        }
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader( new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
                notifyPotentialLineListeners(line);
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

    public interface LineListener {
        void onLineOutput(String line);
    }

}
