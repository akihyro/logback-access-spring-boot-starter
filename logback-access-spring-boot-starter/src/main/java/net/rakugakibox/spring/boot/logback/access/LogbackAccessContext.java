package net.rakugakibox.spring.boot.logback.access;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import ch.qos.logback.access.joran.JoranConfigurator;
import ch.qos.logback.access.spi.AccessContext;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.FilterReply;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import net.rakugakibox.spring.boot.logback.access.undertow.UAbstractLogbackAccessEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;
import static org.springframework.util.ClassUtils.addResourcePathToPackagePath;

/**
 * The Logback-access context.
 */
@Slf4j
public class LogbackAccessContext extends AccessContext {

    /**
     * The default locations of the configuration file.
     */
    private static final List<String> DEFAULT_CONFIGS = unmodifiableList(asList(
            "classpath:logback-access-test.xml",
            "classpath:logback-access.xml",
            "classpath:logback-access-test-spring.xml",
            "classpath:logback-access-spring.xml"
    ));

    /**
     * The fallback location of the configuration file.
     */
    private static final String FALLBACK_CONFIG =
            "classpath:" + addResourcePathToPackagePath(LogbackAccessContext.class, "logback-access-spring.xml");

    /**
     * The configuration properties for Logback-access.
     */
    private final LogbackAccessProperties logbackAccessProperties;

    /**
     * The environment.
     */
    private final Environment environment;

    /**
     * The application event publisher.
     */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Constructs an instance.
     *
     * @param logbackAccessProperties the configuration properties for Logback-access.
     * @param environment the environment.
     * @param applicationEventPublisher the application event publisher.
     */
    public LogbackAccessContext(
            LogbackAccessProperties logbackAccessProperties,
            Environment environment,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.logbackAccessProperties = logbackAccessProperties;
        this.environment = environment;
        this.applicationEventPublisher = applicationEventPublisher;
        setName(CoreConstants.DEFAULT_CONTEXT_NAME);
    }

    /**
     * Configures by the configuration files.
     * Cause exceptions is wrapped with {@link LogbackAccessConfigurationException}.
     * If configuration file is absent, applies the fallback configuration ({@link #FALLBACK_CONFIG}).
     *
     * @throws LogbackAccessConfigurationException if an exception occurs.
     */
    public void configure() throws LogbackAccessConfigurationException {
        if (logbackAccessProperties.getConfig() != null) {
            configure(logbackAccessProperties.getConfig());
            return;
        }
        for (String config : DEFAULT_CONFIGS) {
            if (configureIfPresent(config)) {
                return;
            }
        }
        configure(FALLBACK_CONFIG);
    }

    /**
     * Configures by the configuration file.
     * Cause exceptions is wrapped with {@link LogbackAccessConfigurationException}.
     * If configuration file is absent, throws an exception ({@link LogbackAccessConfigurationException}).
     *
     * @param config the location of the configuration file.
     * @throws LogbackAccessConfigurationException if an exception occurs.
     */
    private void configure(String config) throws LogbackAccessConfigurationException {
        try {
            configureWithCauseThrowing(config);
        } catch (Exception exc) {
            throw new LogbackAccessConfigurationException(this, config, exc);
        }
    }

    /**
     * Configures by the configuration file.
     * Cause exceptions is wrapped with {@link LogbackAccessConfigurationException}.
     * If configuration file is absent, skips and returns {@code false}.
     *
     * @param config the location of the configuration file.
     * @return {@code true} if successful, {@code false} if the configuration file is absent.
     * @throws LogbackAccessConfigurationException if an exception occurs.
     */
    private boolean configureIfPresent(String config) throws LogbackAccessConfigurationException {
        try {
            configureWithCauseThrowing(config);
            return true;
        } catch (FileNotFoundException exc) {
            return false;
        } catch (Exception exc) {
            throw new LogbackAccessConfigurationException(this, config, exc);
        }
    }

    /**
     * Configures by the configuration file.
     * Cause exceptions is not wrapped with {@link LogbackAccessConfigurationException}.
     * If configuration file is absent, throws an exception ({@link FileNotFoundException}).
     *
     * @param config the location of the configuration file.
     * @throws IOException if an I/O exception occurs.
     * @throws JoranException if a {@link JoranConfigurator} exception occurs.
     */
    private void configureWithCauseThrowing(String config) throws IOException, JoranException {
        URL url = ResourceUtils.getURL(config);
        @Cleanup InputStream stream = url.openStream();
        LogbackAccessJoranConfigurator configurator = new LogbackAccessJoranConfigurator(environment);
        configurator.setContext(this);
        configurator.doConfigure(stream);
        log.info("Configured the Logback-access: context=[{}], config=[{}]", this, config);
    }

    /**
     * Emits the Logback-access event.
     *
     * @param event the Logback-access event.
     */
    public void emit(AbstractLogbackAccessEvent event) {
        event.setUseServerPortInsteadOfLocalPort(logbackAccessProperties.isUseServerPortInsteadOfLocalPort());
        if (getFilterChainDecision(event) != FilterReply.DENY) {
            callAppenders(event);
            applicationEventPublisher.publishEvent(new LogbackAccessAppendedEvent(this, event));
        } else {
            applicationEventPublisher.publishEvent(new LogbackAccessDeniedEvent(this, event));
        }
    }
    
    public void emit(UAbstractLogbackAccessEvent event) {
        event.setUseServerPortInsteadOfLocalPort(logbackAccessProperties.isUseServerPortInsteadOfLocalPort());
        if (getFilterChainDecision(event) != FilterReply.DENY) {
            callAppenders(event);
            applicationEventPublisher.publishEvent(new LogbackAccessAppendedEvent(this, event));
        } else {
            applicationEventPublisher.publishEvent(new LogbackAccessDeniedEvent(this, event));
        }
    }

}
