package net.rakugakibox.spring.boot.logback.access;

import net.rakugakibox.spring.boot.logback.access.test.AbstractWebContainerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The base class for testing to disable {@link LogbackAccessAutoConfiguration}.
 */
@RunWith(Parameterized.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@SpringBootTest(value = "logback.access.enabled=false", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogbackAccessAutoConfigurationDisablingTest extends AbstractWebContainerTest {

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
    protected Optional<FilterRegistrationBean> logbackAccessSecurityAttributesSaveFilter;

    @Test
    public void properties_should_be_empty() {
        assertThat(logbackAccessProperties).isEmpty();
    }

    @Test
    public void installer_should_be_empty() {
        assertThat(installer).isEmpty();
    }

    @Test
    public void security_filter_should_be_empty() {
        assertThat(logbackAccessSecurityAttributesSaveFilter).isEmpty();
    }

}
