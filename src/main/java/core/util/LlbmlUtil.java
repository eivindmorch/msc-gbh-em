package core.util;

public abstract class LlbmlUtil {

    public static double normaliseDegForLlbml(double deg) {
        deg = 360 - deg; // Convert from counter-clockwise to clockwise
        deg += 90;       // Make north 0 degrees instead of east
        return deg;
    }
}
