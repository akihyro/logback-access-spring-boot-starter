package net.rakugakibox.springbootext.logback.access;

import ch.qos.logback.access.joran.JoranConfigurator;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * The Logback-access configurator.
 */
@ConfigurationProperties(prefix = "logback.access")
@Slf4j
public class LogbackAccessConfigurator {

    /**
     * The default locations of the configuration file.
     */
    private static final String[] DEFAULT_CONFIGS = new String[]{"classpath:logback-access-test.xml", "classpath:logback-access.xml",};

    /**
     * The fallback location of the configuration file.
     */
    private static final String FALLBACK_CONFIG = "classpath:" + ClassUtils.addResourcePathToPackagePath(
            LogbackAccessConfigurator.class,
            "logback-access.xml");

    /**
     * The location of the configuration file.
     */
    @Getter
    @Setter
    private String config;

    /**
     * Enable requestAttributes in the Tomcat Valve. Defaults to false.
     */
    @Getter
    @Setter
    private Boolean enableRequestAttributes = false;

    /**
     * Configures the Logback-access.
     *
     * @param context the Logback-access context.
     */
    public void configure(Context context) {

        if (StringUtils.hasLength(config)) {
            try {
                configure(context, config);
                return;
            } catch (IOException | JoranException exc) {
                throw createException(context, config, exc);
            }
        }

        for (String defaultConfig : DEFAULT_CONFIGS) {
            try {
                configure(context, defaultConfig);
                return;
            } catch (IOException exc) {
                // The default configuration files is optional.
                log.debug("Skipped a default configuration file: config=[{}] with exc=[{}]", defaultConfig, exc);
            } catch (JoranException exc) {
                throw createException(context, defaultConfig, exc);
            }
        }

        try {
            configure(context, FALLBACK_CONFIG);
        } catch (IOException | JoranException exc) {
            throw createException(context, FALLBACK_CONFIG, exc);
        }

    }

    /**
     * Configures the Logback-access.
     *
     * @param context the Logback-access context.
     * @param config  the location of the configuration file.
     * @throws IOException    if an I/O exception occurs.
     * @throws JoranException if a {@link JoranConfigurator} exception occurs.
     */
    private void configure(Context context, String config) throws IOException, JoranException {
        URL url = ResourceUtils.getURL(config);
        @Cleanup InputStream stream = url.openStream();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(stream);
        log.info("Configured the Logback-access: context=[{}] from config=[{}]", context, config);
    }

    /**
     * Creates an exception.
     *
     * @param context the context.
     * @param config  the location of the configuration file.
     * @param cause   the cause of exception.
     * @return an exception.
     */
    private RuntimeException createException(Context context, String config, Exception cause) {
        return new IllegalStateException(
                "Could not configure the Logback-access: context=[" + context + "] from config=[" + config + "]",
                cause);
    }

}
