package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SimEngine implements Runnable {

    private Logger logger = LoggerFactory.getLogger(SimEngine.class);

    private volatile boolean keepalive;
    private volatile boolean running;
    private Process process;

    // Todo make SimEngine able to load and reload scenario after init
    public SimEngine() {
    }

    public void init() {
        this.keepalive = true;
        this.running = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        String vrfDir = System.getenv("MAK_VRFDIR64");
        String vrfBin64 = vrfDir + "/bin64/";
        String scenarioPath = vrfDir + "/userData/scenarios/it3903/follow_time-contrained-earth.scnx";

        String executable = vrfBin64 + "vrfSimHLA1516e.exe";
        String appNr = "--appNumber 3001";
        String siteId = "--siteId 1";
        String timeManagement = "--timeManagement";
        String fedName = "--execName rlo";
        String fedFile = "--fedFileName RPR_FOM_v2.0_1516-2010.xml";
        String scenario = "--scenarioFileName " + scenarioPath;
        String rprVersion = "--rprFomVersion 2.0";
        String plugins = "--loadPlugin LLBMLSimHLA1516e_VC10.dll";
        String runMode = "--startInRunMode";

        ArrayList<String> fomModules = new ArrayList<>();
        fomModules.add("--fomModules MAK-VRFExt-1_evolved.xml");
        fomModules.add("--fomModules MAK-DIGuy-2_evolved.xml");
        fomModules.add("--fomModules MAK-LgrControl-1_evolved.xml");
        fomModules.add("--fomModules MAK-VRFAggregate-1_evolved.xml");
        fomModules.add("--fomModules MAK-DynamicTerrain-1_evolved.xml");
        fomModules.add("--fomModules LLBML_v2_6.xml");

        ArrayList<String> processParams= new ArrayList<>();
        processParams.addAll(Arrays.asList(executable, appNr, siteId, timeManagement, fedName, fedFile, scenario));
        processParams.addAll(fomModules);
        processParams.addAll(Arrays.asList(rprVersion, plugins, runMode));

        try {
            logger.info("Running simulation engine with parameters: " + paramsToString(processParams));
            ProcessBuilder processBuilder = new ProcessBuilder(processParams);
            processBuilder.directory(new File(vrfBin64));
            process = processBuilder.start();

            final Thread consoleOutputThread = new Thread(() -> {
                try {
                    final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null || running) {
                        logger.info(line);
                    }
                    reader.close();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            });
            consoleOutputThread.start();

            while (keepalive) {
            }
            try {
                TimeUnit.SECONDS.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("keepalive false");
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bufferedWriter.write("q\n");
            bufferedWriter.flush();
            bufferedWriter.close();
            System.out.println("sending shutdown command complete");


//            this.running = false;
//            process.destroy();
//            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String paramsToString(List<String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < params.size() - 1; i++) {
            stringBuilder.append(params.get(i));
            stringBuilder.append(" ");
        }
        stringBuilder.append(params.get(params.size() - 1));
        return stringBuilder.toString();
    }

    void destroy() {
        logger.info("Destroying simulation engine.");
        this.keepalive = false;
    }

}
