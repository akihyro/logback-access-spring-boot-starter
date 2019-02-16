package net.rakugakibox.spring.boot.logback.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rakugakibox.spring.boot.logback.access.LogbackAccessProperties.TeeFilterProperties;
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

import ch.qos.logback.access.servlet.TeeFilter;

import static ch.qos.logback.access.AccessConstants.TEE_FILTER_INCLUDES_PARAM;
import static ch.qos.logback.access.AccessConstants.TEE_FILTER_EXCLUDES_PARAM;

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
    @RequiredArgsConstructor
    @ConditionalOnBean(value = ConfigurableTomcatWebServerFactory.class)
    @Configuration
    public static class ForTomcat {

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
    @RequiredArgsConstructor
    @ConditionalOnBean(value = ConfigurableJettyWebServerFactory.class)
    @Configuration
    public static class ForJetty {

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
    @RequiredArgsConstructor
    @ConditionalOnBean(value = ConfigurableUndertowWebServerFactory.class)
    @Configuration
    public static class ForUndertow {

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

    /**
     * With TeeFilter to log full request and response payloads.
     */
    @RequiredArgsConstructor
    @Configuration
    @ConditionalOnProperty(prefix = "logback.access.tee-filter", name = "enabled", matchIfMissing = false)
    public static class WithTeeFilter {

        /**
         * The configuration properties for Logback-access.
         */
        private final LogbackAccessProperties logbackAccessProperties;

        /**
         * Creates the TeeFilter required to log full request and response payloads.
         * 
         * @return a TeeFilter required to log full request and response payloads.
         */
        @ConditionalOnMissingBean
        @Bean
        public FilterRegistrationBean<TeeFilter> logbackTeeFilter() {
            TeeFilter filter = new TeeFilter();
            FilterRegistrationBean<TeeFilter> filterRegistrationBean = new FilterRegistrationBean<>(filter);
            TeeFilterProperties props = logbackAccessProperties.getTeeFilter();
            filterRegistrationBean.addInitParameter(TEE_FILTER_INCLUDES_PARAM, props.getIncludes());
            filterRegistrationBean.addInitParameter(TEE_FILTER_EXCLUDES_PARAM, props.getExcludes());
            return filterRegistrationBean;
        }

    }

}
