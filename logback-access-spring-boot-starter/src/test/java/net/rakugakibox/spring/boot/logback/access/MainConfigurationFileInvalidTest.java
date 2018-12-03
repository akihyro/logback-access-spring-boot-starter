package net.rakugakibox.spring.boot.logback.access;

import ch.qos.logback.core.joran.spi.JoranException;
import net.rakugakibox.spring.boot.logback.access.test.ClassPathRule;
import net.rakugakibox.spring.boot.logback.access.test.ContainerType;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.boot.SpringApplication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * The base class for testing in the case where main configuration files is invalid.
 */
@RunWith(Parameterized.class)
public class MainConfigurationFileInvalidTest {

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<?> data() {
        return ContainerType.all();
    }

    @Parameterized.Parameter
    public ContainerType containerType;

    /**
     * Creates a class test rule.
     *
     * @return a class test rule.
     */
    @ClassRule
    public static TestRule classRule() {
        return new ClassPathRule(MainConfigurationFileInvalidTest.class);
    }

    /**
     * Tests a Logback-access configuration exception in the case where the main configuration file is invalid.
     */
    @Test
    public void application_startup_should_fail_on_invalid_configuration_file() {

        SpringApplication application = containerType.buildApplication();
        Throwable throwable = catchThrowable(application::run);
        Optional<LogbackAccessConfigurationException> exc = findLogbackAccessConfigurationException(throwable);

        assertThat(exc).hasValueSatisfying(value ->
                assertThat(value)
                        .hasMessageStartingWith("Could not configure Logback-access:")
                        .hasMessageContaining("config=[classpath:logback-access.xml]")
                        .hasCauseInstanceOf(JoranException.class)
        );

    }

    /**
     * Finds the {@link LogbackAccessConfigurationException}.
     *
     * @param throwable the throwable.
     * @return the Logback-access configuration exception.
     */
    private Optional<LogbackAccessConfigurationException> findLogbackAccessConfigurationException(Throwable throwable) {
        while (throwable != null && !(throwable instanceof LogbackAccessConfigurationException)) {
            throwable = throwable.getCause();
        }
        return Optional.ofNullable((LogbackAccessConfigurationException) throwable);
    }

}
