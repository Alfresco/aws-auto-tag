package org.alfresco.aws.lambda.autotag;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Object to hold incoming cloudwatch event data.
 */
public class CloudWatchEvent implements Serializable {

    private String version;
    private String id;
    private String source;
    private String account;
    private String time;
    private String region;
    private List<Object> resources;
    private Map<String, Object> detail;

    public CloudWatchEvent() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Map<String, Object> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, Object> detail) {
        this.detail = detail;
    }

    public List<Object> getResources() {
        return resources;
    }

    public void setResources(List<Object> resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return "CloudWatchEvent{" +
                "version='" + version + '\'' +
                ", id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", account='" + account + '\'' +
                ", time='" + time + '\'' +
                ", region='" + region + '\'' +
                ", resources=" + resources +
                ", detail=" + detail +
                '}';
    }
}
