package org.java2uml.java2umlapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Stores App metadata properties.
 *
 * @author kawaiifox
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    String version;
    String name;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
