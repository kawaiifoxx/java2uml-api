package org.java2uml.java2umlapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@EnableJpaRepositories
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class AppConfig {
    private final AppProperties appProperties;

    public AppConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
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
}
