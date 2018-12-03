package net.rakugakibox.spring.boot.logback.access.test.config;

import net.rakugakibox.spring.boot.logback.access.test.ContainerType;
import org.springframework.boot.web.embedded.jetty.JettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The configuration for using {@link JettyReactiveWebServerFactory}.
 */
@Configuration
@Import(GenericReactiveWebServerConfiguration.class)
public class JettyReactiveWebServerConfiguration {

    /**
     * Creates a {@link JettyReactiveWebServerFactory}.
     *
     * @return a {@link JettyReactiveWebServerFactory}.
     */
    @Bean
    public JettyReactiveWebServerFactory jettyReactiveWebServerFactory() {
        return new JettyReactiveWebServerFactory();
    }

    @Bean
    public ContainerType containerType() {
        return ContainerType.REACTIVE_JETTY;
    }

}
