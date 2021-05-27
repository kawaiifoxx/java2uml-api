package org.java2uml.java2umlapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class ServerConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    private final Logger logger = LoggerFactory.getLogger(ServerConfig.class);

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        String portStr = null;
        try {
            portStr = System.getenv("PORT");
            if (portStr == null) {
                logger.info("$PORT not defined in system environment.");
                logger.info("Setting port to 8080");
                portStr = "8080";
            }
        } catch (SecurityException e) {
            logger.warn("Unable to get system variable $PORT");
            portStr = "8080";
        } finally {
            factory.setPort(Integer.parseInt(portStr == null ? "8080" : portStr));
        }

    }
}
