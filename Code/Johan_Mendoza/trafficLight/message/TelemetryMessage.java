package it.unimore.dipi.iot.fleet.message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project mqtt-demo-fleet-monitoring
 * @created 04/11/2020 - 15:56
 */
public class TelemetryMessage<T> {

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("type")
    private String type;

    @JsonProperty("data")
    private T dataValue;

    public TelemetryMessage() {
    }

    public TelemetryMessage(String type, T dataValue) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.dataValue = dataValue;
    }

    public TelemetryMessage(long timestamp, String type, T dataValue) {
        this.timestamp = timestamp;
        this.type = type;
        this.dataValue = dataValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getDataValue() {
        return dataValue;
    }

    public void setDataValue(T dataValue) {
        this.dataValue = dataValue;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TelemetryMessage{");
        sb.append("timestamp=").append(timestamp);
        sb.append(", type='").append(type).append('\'');
        sb.append(", dataValue=").append(dataValue);
        sb.append('}');
        return sb.toString();
    }
}
