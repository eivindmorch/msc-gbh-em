package core.simulation;

public class SimSettings {


    // -- SETUP ---------------------------------------------------------------------
        public static String vrfDirectory = System.getenv("MAK_VRFDIR64");
        public static int siteId = 1;
        public static String federationName = "rlo";
        public static String federationFile = "RPR_FOM_v2.0_1516-2010.xml";
        public static String rprFomVersion = "2.0";

        public static String[] fomModules = new String[] {
                "MAK-VRFExt-1_evolved.xml",
                "MAK-DIGuy-2_evolved.xml",
                "MAK-LgrControl-1_evolved.xml",
                "MAK-VRFAggregate-1_evolved.xml",
                "MAK-DynamicTerrain-1_evolved.xml",
                "LLBML_v2_6.xml",
                "RemoteControl.xml"
        };


        // SimEngine
        public static int simEngineApplicationNumber = 3001;
        public static boolean useTimeManagement = true;
        public static boolean startInRunMode = false;

        public static String[] plugins = new String[] {
                "LLBMLSimHLA1516e_VC10.dll",
                "VrfCgfControl.dll"
        };


        // SimGUI
        public static int simGUIApplicationNumber = 3101;


        // RTI
        public static final String rtiDirectory = System.getenv("MAK_RTIDIR");
    // ------------------------------------------------------------------------------


    // -- OPERATION -----------------------------------------------------------------
        public static final int simulationTickDelayInMilliseconds = 0;
    // ------------------------------------------------------------------------------

}
