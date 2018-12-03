package net.rakugakibox.spring.boot.logback.access.test.config;

import net.rakugakibox.spring.boot.logback.access.test.ContainerType;
import org.springframework.boot.web.embedded.undertow.UndertowReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The configuration for using {@link UndertowReactiveWebServerFactory}.
 */
@Configuration
@Import(GenericReactiveWebServerConfiguration.class)
public class UndertowReactiveWebServerConfiguration {

    /**
     * Creates a {@link UndertowReactiveWebServerFactory}.
     *
     * @return a {@link UndertowReactiveWebServerFactory}.
     */
    @Bean
    public UndertowReactiveWebServerFactory undertowReactiveWebServerFactory() {
        return new UndertowReactiveWebServerFactory();
    }

    @Bean
    public ContainerType containerType() {
        throw new UnsupportedOperationException();
    }

}
