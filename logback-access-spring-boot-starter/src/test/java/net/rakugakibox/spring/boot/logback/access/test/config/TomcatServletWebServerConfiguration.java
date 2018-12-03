package net.rakugakibox.spring.boot.logback.access.test.config;

import net.rakugakibox.spring.boot.logback.access.test.ContainerType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration for using {@link TomcatServletWebServerFactory}.
 */
@Configuration
@EnableAutoConfiguration
public class TomcatServletWebServerConfiguration {

    /**
     * Creates a {@link TomcatServletWebServerFactory}.
     *
     * @return a {@link TomcatServletWebServerFactory}.
     */
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }

    @Bean
    public ContainerType containerType() {
        return ContainerType.TOMCAT;
    }

}
