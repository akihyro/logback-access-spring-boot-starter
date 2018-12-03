package net.rakugakibox.spring.boot.logback.access.tomcat;

import ch.qos.logback.access.spi.AccessEvent;
import ch.qos.logback.access.tomcat.TomcatServerAdapter;
import lombok.Getter;
import lombok.Setter;
import net.rakugakibox.spring.boot.logback.access.AbstractLogbackAccessEvent;
import net.rakugakibox.spring.boot.logback.access.AbstractOverridenAttribute;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Globals;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.AbstractAccessLogValve;
import org.apache.catalina.valves.RemoteIpValve;

import java.util.Optional;

/**
 * The Logback-access event for Tomcat.
 */
public class TomcatLogbackAccessEvent extends AbstractLogbackAccessEvent {

    /**
     * Whether to enable request attributes to work with the {@link RemoteIpValve}.
     */
    @Getter
    @Setter
    private boolean requestAttributesEnabled;

    /**
     * The local port.
     */
    private final LocalPort localPort = new LocalPort();

    /**
     * The remote address.
     */
    private final RemoteAddr remoteAddr = new RemoteAddr();

    /**
     * The remote host.
     */
    private final RemoteHost remoteHost = new RemoteHost();

    /**
     * The protocol.
     */
    private final Protocol protocol = new Protocol();

    /**
     * Constructs an instance.
     *
     * @param request the HTTP request.
     * @param response the HTTP response.
     */
    public TomcatLogbackAccessEvent(Request request, Response response) {
        super(request, response, new ServerAdapter(request, response));
    }

    /** {@inheritDoc} */
    @Override
    public int getLocalPort() {
        return localPort.get();
    }

    /** {@inheritDoc} */
    @Override
    public String getRemoteAddr() {
        return remoteAddr.get();
    }

    /** {@inheritDoc} */
    @Override
    public String getRemoteHost() {
        return remoteHost.get();
    }

    /** {@inheritDoc} */
    @Override
    public String getProtocol() {
        return protocol.get();
    }

    /**
     * The local port.
     * Takes the server port in the same way as {@link AbstractAccessLogValve} if necessary.
     */
    private class LocalPort extends AbstractOverridenAttribute<Integer> {

        /** {@inheritDoc} */
        @Override
        protected Optional<Integer> evaluateValueToOverride() {
            return Optional.of(TomcatLogbackAccessEvent.this)
                    .filter(AbstractLogbackAccessEvent::isUseServerPortInsteadOfLocalPort)
                    .filter(TomcatLogbackAccessEvent::isRequestAttributesEnabled)
                    .map(AccessEvent::getRequest)
                    .map(request -> (Integer) request.getAttribute(AccessLog.SERVER_PORT_ATTRIBUTE));
        }

        /** {@inheritDoc} */
        @Override
        protected Integer getOriginalValue() {
            return TomcatLogbackAccessEvent.super.getLocalPort();
        }

    }

    /**
     * The remote address.
     * Takes in the same way as {@link AbstractAccessLogValve}.
     */
    private class RemoteAddr extends AbstractOverridenAttribute<String> {

        /** {@inheritDoc} */
        @Override
        protected Optional<String> evaluateValueToOverride() {
            return Optional.of(TomcatLogbackAccessEvent.this)
                    .filter(TomcatLogbackAccessEvent::isRequestAttributesEnabled)
                    .map(AccessEvent::getRequest)
                    .map(request -> (String) request.getAttribute(AccessLog.REMOTE_ADDR_ATTRIBUTE));
        }

        /** {@inheritDoc} */
        @Override
        protected String getOriginalValue() {
            return TomcatLogbackAccessEvent.super.getRemoteAddr();
        }

    }

    /**
     * The remote host.
     * Takes in the same way as {@link AbstractAccessLogValve}.
     */
    private class RemoteHost extends AbstractOverridenAttribute<String> {

        /** {@inheritDoc} */
        @Override
        protected Optional<String> evaluateValueToOverride() {
            return Optional.of(TomcatLogbackAccessEvent.this)
                    .filter(TomcatLogbackAccessEvent::isRequestAttributesEnabled)
                    .map(AccessEvent::getRequest)
                    .map(request -> (String) request.getAttribute(AccessLog.REMOTE_HOST_ATTRIBUTE));
        }

        /** {@inheritDoc} */
        @Override
        protected String getOriginalValue() {
            return TomcatLogbackAccessEvent.super.getRemoteHost();
        }

    }

    /**
     * The protocol.
     * Takes in the same way as {@link AbstractAccessLogValve}.
     */
    private class Protocol extends AbstractOverridenAttribute<String> {

        /** {@inheritDoc} */
        @Override
        protected Optional<String> evaluateValueToOverride() {
            return Optional.of(TomcatLogbackAccessEvent.this)
                    .filter(TomcatLogbackAccessEvent::isRequestAttributesEnabled)
                    .map(AccessEvent::getRequest)
                    .map(request -> (String) request.getAttribute(AccessLog.PROTOCOL_ATTRIBUTE));
        }

        /** {@inheritDoc} */
        @Override
        protected String getOriginalValue() {
            return TomcatLogbackAccessEvent.super.getProtocol();
        }

    }

    /**
     * The server adapter.
     * Takes the content length in the same way as {@link AbstractAccessLogValve}.
     */
    private static class ServerAdapter extends TomcatServerAdapter {

        /**
         * The HTTP request.
         */
        private final Request request;

        /**
         * The HTTP response.
         */
        private final Response response;

        /**
         * Constructs an instance.
         *
         * @param request the HTTP request.
         * @param response the HTTP response.
         */
        private ServerAdapter(Request request, Response response) {
            super(request, response);
            this.request = request;
            this.response = response;
        }

        /** {@inheritDoc} */
        @Override
        public long getContentLength() {
            long length = response.getBytesWritten(false);
            if (length > 0) {
                return length;
            }
            Long start = (Long) request.getAttribute(Globals.SENDFILE_FILE_START_ATTR);
            Long end = (Long) request.getAttribute(Globals.SENDFILE_FILE_END_ATTR);
            if (start != null && end != null) {
                return end - start;
            }
            return super.getContentLength();
        }

    }

}
