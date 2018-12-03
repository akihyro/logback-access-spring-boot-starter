package net.rakugakibox.spring.boot.logback.access.test;

import net.rakugakibox.spring.boot.logback.access.test.config.*;
import org.springframework.boot.SpringApplication;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.SocketUtils.findAvailableTcpPort;

public enum ContainerType {

    TOMCAT              (TomcatServletWebServerConfiguration.class),
    REACTIVE_TOMCAT     (TomcatReactiveWebServerConfiguration.class),

    UNDERTOW            (UndertowServletWebServerConfiguration.class),
    REACTIVE_UNDERTOW   (UndertowReactiveWebServerConfiguration.class),

    JETTY               (JettyServletWebServerConfiguration.class),
    REACTIVE_JETTY      (JettyReactiveWebServerConfiguration.class),

    REACTOR_NETTY       (NettyReactiveWebServerConfiguration.class)
    ;

    private final Class<?> configurationClass;

    ContainerType(Class<?> containerConfiguration) {
        this.configurationClass = containerConfiguration;
    }

    public Class<?> getConfigurationClass() {
        return configurationClass;
    }

    public static List<ContainerType> servlet() {
        return Arrays.asList(TOMCAT, UNDERTOW, JETTY);
    }

    public static List<ContainerType> all() {
        List<ContainerType> all = new ArrayList<>(Arrays.asList(values()));
        all.removeAll(disabled());

        return all;
    }

    public static List<ContainerType> disabled() {
        // it seems that Reactive Undertow just ignores DeploymentInfoCustomizer :)
        return Arrays.asList(REACTIVE_UNDERTOW, REACTOR_NETTY);
    }

    public static List<ContainerType> reactive() {
        return Arrays.stream(values())
              .filter(ContainerType::isReactive)
              .collect(Collectors.toList());
    }

    public boolean isReactive() {
        return name().startsWith("REACT");
    }

    public boolean isTomcat() {
        return name().contains("TOMCAT");
    }

    public boolean isUndertow() {
        return name().contains("UNDERTOW");
    }

    public boolean isJetty() {
        return name().contains("JETTY");
    }

    public SpringApplication buildApplication(String... additionalProperties) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("server.port", findAvailableTcpPort());
        for (int i = 0; i < additionalProperties.length / 2; i += 2) {
            properties.put(additionalProperties[i], additionalProperties[i + 1]);
        }

        SpringApplication application = new SpringApplication(configurationClass);
        application.setDefaultProperties(properties);
        if (isReactive()) {
            properties.put("spring.main.web-application-type", "REACTIVE");
        }

        return application;
    }

}
