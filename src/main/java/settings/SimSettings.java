package settings;

public class SimSettings {

    // RTI
    public static final String rtiDirectory = System.getenv("MAK_RTIDIR");

    // SimEngine
    public static String vrfDirectory = System.getenv("MAK_VRFDIR64");
    public static String scenario = "it3903/follow_time-contrained-earth.scnx";

    public static int siteId = 1;
    public static int applicationNumber = 3001;
    public static String federationName = "rlo";
    public static String federationFile = "RPR_FOM_v2.0_1516-2010.xml";
    public static String rprFomVersion = "2.0";
    public static boolean useTimeManagement = true;
    public static boolean startInRunMode = false;

    public static String[] plugins = new String[] {
            "LLBMLSimHLA1516e_VC10.dll"
    };

    public static String[] fomModules = new String[] {
            "MAK-VRFExt-1_evolved.xml",
            "MAK-DIGuy-2_evolved.xml",
            "MAK-LgrControl-1_evolved.xml",
            "MAK-VRFAggregate-1_evolved.xml",
            "MAK-DynamicTerrain-1_evolved.xml",
            "LLBML_v2_6.xml"
    };

    // General
    public static final int simulationTickDelayInMilliseconds = 200;

}
