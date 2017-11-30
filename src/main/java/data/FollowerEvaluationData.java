package data;

import util.Geometer;

import java.util.List;

public class FollowerEvaluationData extends Data {

    private double distanceToTarget;

    public FollowerEvaluationData() {
    }

    public FollowerEvaluationData(List<String> csvElements) {
        this.timestamp = Double.valueOf(csvElements.get(0));
        this.distanceToTarget = Double.valueOf(csvElements.get(1));
    }

    public void setValues(double timestamp, RawData rawData1, RawData rawData2) {
        this.timestamp = timestamp;
        this.distanceToTarget = Geometer.distance(rawData1.getLla(), rawData2.getLla());
    }

    @Override
    public String getHeader() {
        return "distance to target";
    }

    @Override
    public String getValuesAsCsvString() {
        return String.valueOf(distanceToTarget);
    }

    @Override
    public String toString() {
        return "Timestamp: " + timestamp + ", " + "Distance to target: " + distanceToTarget;
    }
}
