package it.unimore.dipi.iot.fleet.message;

import java.util.Map;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project mqtt-demo-fleet-monitoring
 * @created 04/11/2020 - 15:58
 */
public abstract class GenericMessage {

    private String type;

    private long timestamp;

    private Map<String, Object> metadata;

    public GenericMessage() {
    }

    public GenericMessage(String type, Map<String, Object> metadata) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.metadata = metadata;
    }

    public GenericMessage(String type, long timestamp, Map<String, Object> metadata) {
        this.type = type;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EventMessage{");
        sb.append("type='").append(type).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", metadata=").append(metadata);
        sb.append('}');
        return sb.toString();
    }
}
