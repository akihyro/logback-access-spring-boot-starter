package net.rakugakibox.spring.boot.logback.access.undertow;

import net.rakugakibox.spring.boot.logback.access.AbstractTestSpringConfigurationFileInvalidTest;
import net.rakugakibox.spring.boot.logback.access.test.UndertowServletWebServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The test in the case where test spring configuration files is invalid for Undertow.
 */
public class UndertowTestSpringConfigurationFileInvalidTest extends AbstractTestSpringConfigurationFileInvalidTest {

    /**
     * Constructs an instance.
     */
    public UndertowTestSpringConfigurationFileInvalidTest() {
        super(ContextConfiguration.class);
    }

    /**
     * The context configuration.
     */
    @Configuration
    @Import(UndertowServletWebServerConfiguration.class)
    public static class ContextConfiguration extends AbstractContextConfiguration {
    }

}
