package net.rakugakibox.spring.boot.logback.access;

import net.rakugakibox.spring.boot.logback.access.jetty.JettyLogbackAccessInstaller;
import net.rakugakibox.spring.boot.logback.access.test.AbstractWebContainerTest;
import net.rakugakibox.spring.boot.logback.access.test.ContainerType;
import net.rakugakibox.spring.boot.logback.access.tomcat.TomcatLogbackAccessInstaller;
import net.rakugakibox.spring.boot.logback.access.undertow.UndertowLogbackAccessInstaller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The base class for testing {@link LogbackAccessAutoConfiguration}.
 */
@RunWith(Parameterized.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogbackAccessAutoConfigurationTest extends AbstractWebContainerTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { ContainerType.TOMCAT, TomcatLogbackAccessInstaller.class },
                { ContainerType.UNDERTOW, UndertowLogbackAccessInstaller.class },
                { ContainerType.JETTY, JettyLogbackAccessInstaller.class }
        });
    }

    @Parameterized.Parameter
    public ContainerType containerType;

    @Parameterized.Parameter(1)
    public Class<? extends AbstractLogbackAccessInstaller> installerClass;

    /**
     * The configuration properties for Logback-access.
     */
    @Autowired
    private Optional<LogbackAccessProperties> logbackAccessProperties;

    /**
     * The Logback-access installer.
     */
    @Autowired
    private Optional<AbstractLogbackAccessInstaller> installer;

    /**
     * The filter that saves Spring Security attributes for Logback-access.
     */
    @Autowired
    @Qualifier("logbackAccessSecurityAttributesSaveFilter")
    private Optional<FilterRegistrationBean> logbackAccessSecurityAttributesSaveFilter;

    /**
     * Tests the filter that saves Spring Security attributes for Logback-access.
     */
    @Test
    public void should_insert_required_dependencies() {

        assertThat(logbackAccessProperties).isPresent();
        assertThat(logbackAccessSecurityAttributesSaveFilter).isPresent();
        assertThat(installer).isPresent()
                .containsInstanceOf(installerClass);

    }

}
