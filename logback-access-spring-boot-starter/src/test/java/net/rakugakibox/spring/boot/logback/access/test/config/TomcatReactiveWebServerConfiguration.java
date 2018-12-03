package net.rakugakibox.spring.boot.logback.access.test.config;

import net.rakugakibox.spring.boot.logback.access.test.ContainerType;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The configuration for using {@link TomcatReactiveWebServerFactory}.
 */
@Configuration
@Import(GenericReactiveWebServerConfiguration.class)
public class TomcatReactiveWebServerConfiguration {

    /**
     * Creates a {@link TomcatReactiveWebServerFactory}.
     *
     * @return a {@link TomcatReactiveWebServerFactory}.
     */
    @Bean
    public TomcatReactiveWebServerFactory tomcatServletWebServerFactory() {
        return new TomcatReactiveWebServerFactory();
    }

    @Bean
    public ContainerType containerType() {
        return ContainerType.REACTIVE_TOMCAT;
    }

}
