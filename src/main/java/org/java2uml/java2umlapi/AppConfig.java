package org.java2uml.java2umlapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.java2uml.java2umlapi.config.AppProperties;
import org.java2uml.java2umlapi.executor.ExecutorWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.filter.ForwardedHeaderFilter;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableJpaRepositories
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class AppConfig {
    private final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private final AppProperties appProperties;
    private final ExecutorWrapper executorWrapper;
    private final Long SHUTDOWN_TIME = 100L;

    public AppConfig(AppProperties appProperties, ExecutorWrapper executorWrapper) {
        this.appProperties = appProperties;
        this.executorWrapper = executorWrapper;
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(appProperties.getName()).version(appProperties.getVersion())
                        .description("Java2UML API is simple web api which provides ability explore java source code.")
                        .license(new License().name("Apache 2.0")
                                .url("https://raw.githubusercontent.com/kawaiifoxx/java2uml-api/main/LICENSE")));
    }

    /**
     * Shutdown ExecutorWrapper before termination.
     * @throws InterruptedException if some thread is interrupted in between then this exception is thrown.
     */
    @PreDestroy
    public void onExitShutdownTaskExecutor() throws InterruptedException {
        logger.info("Shutting Down ExecutorWrapper");
        executorWrapper.shutdown();
        if (!executorWrapper.awaitTermination(SHUTDOWN_TIME, TimeUnit.MILLISECONDS)) {
            logger.warn("ExecutorWrapper did not terminate in specified time.");
            List<Runnable> droppedTasks = executorWrapper.shutdownNow();
            logger.info("Executor was terminated abruptly {} task did not finish", droppedTasks.size());
        }

        logger.info("Shutdown Successfully Finished.");
    }
}
