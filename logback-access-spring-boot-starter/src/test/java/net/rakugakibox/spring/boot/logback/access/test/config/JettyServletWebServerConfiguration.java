package net.rakugakibox.spring.boot.logback.access.test.config;

import net.rakugakibox.spring.boot.logback.access.test.ContainerType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration for using {@link JettyServletWebServerFactory}.
 */
@Configuration
@EnableAutoConfiguration
public class JettyServletWebServerConfiguration {

    /**
     * Creates a {@link JettyServletWebServerFactory}.
     *
     * @return a {@link JettyServletWebServerFactory}.
     */
    @Bean
    public JettyServletWebServerFactory jettyServletWebServerFactory() {
        return new JettyServletWebServerFactory();
    }

    @Bean
    public ContainerType containerType() {
        return ContainerType.JETTY;
    }

}
