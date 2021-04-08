package net.rakugakibox.spring.boot.logback.access.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentInfo;
import lombok.extern.slf4j.Slf4j;
import net.rakugakibox.spring.boot.logback.access.AbstractLogbackAccessInstaller;
import net.rakugakibox.spring.boot.logback.access.LogbackAccessProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;

/**
 * The Logback-access installer for Undertow.
 */
@Slf4j
public class UndertowLogbackAccessInstaller
        extends AbstractLogbackAccessInstaller<UndertowServletWebServerFactory> {

    /**
     * Constructs an instance.
     *
     * @param logbackAccessProperties the configuration properties for Logback-access.
     * @param environment the environment.
     * @param applicationEventPublisher the application event publisher.
     */
    public UndertowLogbackAccessInstaller(
            LogbackAccessProperties logbackAccessProperties,
            Environment environment,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        super(logbackAccessProperties, environment, applicationEventPublisher);
    }

    /** {@inheritDoc} */
    @Override
    protected void installLogbackAccess(UndertowServletWebServerFactory container) {
        container.addBuilderCustomizers(this::enableRecordingRequestStartTime);
        container.addDeploymentInfoCustomizers(this::addUndertowHttpHandlerWrapper);
        log.debug("Installed Logback-access: container=[{}]", container);
    }

    /**
     * Enables recording the request start time.
     *
     * @param builder the Undertow builder.
     */
    private void enableRecordingRequestStartTime(Undertow.Builder builder) {
        builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true);
    }

    /**
     * Adds the Undertow HTTP handler wrapper.
     *
     * @param deploymentInfo the Undertow deployment info.
     */
    private void addUndertowHttpHandlerWrapper(DeploymentInfo deploymentInfo) {
        deploymentInfo.addInitialHandlerChainWrapper(this::wrapUndertowHttpHandler);
    }

    /**
     * Wraps the Undertow HTTP handler.
     *
     * @param handler the Undertow HTTP handler.
     */
    private HttpHandler wrapUndertowHttpHandler(HttpHandler handler) {
        return new LogbackAccessUndertowHttpHandler(
                logbackAccessProperties, environment, applicationEventPublisher, handler);
    }

}
