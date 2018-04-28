package core.simulation;

public class SimSettings {


    // -- SETUP ---------------------------------------------------------------------
        public static final String vrfDirectory = System.getenv("MAK_VRFDIR64");
        public static final int siteId = 1;
        public static final String federationName = "rlo";
        public static final String federationFile = "RPR_FOM_v2.0_1516-2010.xml";
        public static final String rprFomVersion = "2.0";

        public static final String[] fomModules = new String[] {
                "MAK-VRFExt-1_evolved.xml",
                "MAK-DIGuy-2_evolved.xml",
                "MAK-LgrControl-1_evolved.xml",
                "MAK-VRFAggregate-1_evolved.xml",
                "MAK-DynamicTerrain-1_evolved.xml",
                "LLBML_v2_6.xml",
                "RemoteControl.xml"
        };


        // SimEngine
        public static final int simEngineApplicationNumber = 3001;
        public static final boolean useTimeManagement = true;
        public static final boolean startInRunMode = false;

        public static final String[] plugins = new String[] {
                "LLBMLSimHLA1516e_VC10.dll",
                "VrfCgfControl.dll"
        };


        // SimGUI
        public static final int simGUIApplicationNumber = 3101;


        // RTI
        public static final String rtiDirectory = System.getenv("MAK_RTIDIR");
    // ------------------------------------------------------------------------------


    // -- OPERATION -----------------------------------------------------------------
        public static final int simulationTickDelayInMilliseconds = 0;
        public static final int secondsToWaitForUnitsBeforeReload = 10;
        public static final int numberOfTicksToCalculateAverageTickTime = 100;
    // ------------------------------------------------------------------------------

}
