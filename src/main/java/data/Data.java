package data;

public abstract class Data {

    double timestamp;

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public abstract String getHeader();

    public abstract String getValuesAsCsvString();
}
