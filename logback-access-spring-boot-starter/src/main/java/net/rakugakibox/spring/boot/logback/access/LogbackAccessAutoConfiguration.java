package net.rakugakibox.spring.boot.logback.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rakugakibox.spring.boot.logback.access.jetty.JettyLogbackAccessInstaller;
import net.rakugakibox.spring.boot.logback.access.tomcat.TomcatLogbackAccessInstaller;
import net.rakugakibox.spring.boot.logback.access.undertow.UndertowLogbackAccessInstaller;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * The auto-configuration for Logback-access.
 */
@Slf4j
@ConditionalOnProperty(name = "logback.access.enabled", matchIfMissing = true)
@ConditionalOnWebApplication
@EnableConfigurationProperties(LogbackAccessProperties.class)
@Configuration
public class LogbackAccessAutoConfiguration {

    /**
     * For Tomcat.
     */
    @ConditionalOnBean(value = ConfigurableTomcatWebServerFactory.class)
    @Configuration
    public static class ForTomcat extends ForContainer {

        public ForTomcat(LogbackAccessProperties logbackAccessProperties, Environment environment, ApplicationEventPublisher applicationEventPublisher) {
            super(logbackAccessProperties, environment, applicationEventPublisher);
        }

        /**
         * Creates a Logback-access installer.
         *
         * @return a Logback-access installer.
         */
        @ConditionalOnMissingBean
        @Bean
        public TomcatLogbackAccessInstaller tomcatLogbackAccessInstaller() {
            TomcatLogbackAccessInstaller installer = new TomcatLogbackAccessInstaller(
                    logbackAccessProperties, environment, applicationEventPublisher);
            log.debug("Created a TomcatLogbackAccessInstaller: [{}]", installer);
            return installer;
        }

    }

    /**
     * For Jetty.
     */
    @ConditionalOnBean(value = ConfigurableJettyWebServerFactory.class)
    @Configuration
    public static class ForJetty extends ForContainer {

        public ForJetty(LogbackAccessProperties logbackAccessProperties, Environment environment, ApplicationEventPublisher applicationEventPublisher) {
            super(logbackAccessProperties, environment, applicationEventPublisher);
        }

        /**
         * Creates a Logback-access installer.
         *
         * @return a Logback-access installer.
         */
        @ConditionalOnMissingBean
        @Bean
        public JettyLogbackAccessInstaller jettyLogbackAccessInstaller() {
            JettyLogbackAccessInstaller installer = new JettyLogbackAccessInstaller(
                    logbackAccessProperties, environment, applicationEventPublisher);
            log.debug("Created a JettyLogbackAccessInstaller: [{}]", installer);
            return installer;
        }

    }

    /**
     * For Undertow.
     */
    @ConditionalOnBean(value = ConfigurableUndertowWebServerFactory.class)
    @Configuration
    public static class ForUndertow extends ForContainer {

        public ForUndertow(LogbackAccessProperties logbackAccessProperties, Environment environment, ApplicationEventPublisher applicationEventPublisher) {
            super(logbackAccessProperties, environment, applicationEventPublisher);
        }

        /**
         * Creates a Logback-access installer.
         *
         * @return a Logback-access installer.
         */
        @ConditionalOnMissingBean
        @Bean
        public UndertowLogbackAccessInstaller undertowLogbackAccessInstaller() {
            UndertowLogbackAccessInstaller installer = new UndertowLogbackAccessInstaller(
                    logbackAccessProperties, environment, applicationEventPublisher);
            log.debug("Created a UndertowLogbackAccessInstaller: [{}]", installer);
            return installer;
        }

    }

    /**
     * Base class for shared models.
     */
    @RequiredArgsConstructor
    private static class ForContainer {

        /**
         * The configuration properties for Logback-access.
         */
        protected final LogbackAccessProperties logbackAccessProperties;

        /**
         * The environment.
         */
        protected final Environment environment;

        /**
         * The application event publisher.
         */
        protected final ApplicationEventPublisher applicationEventPublisher;

    }

    /**
     * For Spring Security.
     */
    @RequiredArgsConstructor
    @ConditionalOnClass(AbstractSecurityWebApplicationInitializer.class)
    @Configuration
    public static class ForSpringSecurity {

        /**
         * Creates a filter that saves Spring Security attributes for Logback-access.
         *
         * @return a filter that saves Spring Security attributes for Logback-access.
         */
        @ConditionalOnMissingBean(name = "logbackAccessSecurityAttributesSaveFilter")
        @Bean("logbackAccessSecurityAttributesSaveFilter")
        public FilterRegistrationBean logbackAccessSecurityAttributesSaveFilter() {
            LogbackAccessSecurityAttributesSaveFilter filter = new LogbackAccessSecurityAttributesSaveFilter();
            FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(filter);
            log.debug("Created a LogbackAccessSecurityAttributesSaveFilter: [{}]", filter);
            return filterRegistrationBean;
        }

    }

}
