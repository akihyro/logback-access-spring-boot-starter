package net.rakugakibox.spring.boot.logback.access.test.config;

import net.rakugakibox.spring.boot.logback.access.test.ContainerType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration for using {@link UndertowServletWebServerFactory}.
 */
@Configuration
@EnableAutoConfiguration
public class UndertowServletWebServerConfiguration {

    /**
     * Creates a {@link UndertowServletWebServerFactory}.
     *
     * @return a {@link UndertowServletWebServerFactory}.
     */
    @Bean
    public UndertowServletWebServerFactory undertowServletWebServerFactory() {
        return new UndertowServletWebServerFactory();
    }

    @Bean
    public ContainerType containerType() {
        return ContainerType.UNDERTOW;
    }

}
