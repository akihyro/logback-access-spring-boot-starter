package net.rakugakibox.spring.boot.logback.access.undertow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;


/**
 * The Logback-access event for Undertow.
 */
public class UndertowLogbackAccessEvent extends UAbstractLogbackAccessEvent {

    /**
     * Constructs an instance.
     *
     * @param exchange the HTTP server exchange.
     */
    public UndertowLogbackAccessEvent(HttpServerExchange exchange) {
        super(extractHttpServletRequest(exchange), extractHttpServletResponse(exchange), exchange, new ServerAdapter(exchange));
    }

    /**
     * Extracts the HTTP servlet request from the HTTP server exchange.
     *
     * @param exchange the HTTP server exchange.
     * @return the HTTP servlet request.
     */
    private static HttpServletRequest extractHttpServletRequest(HttpServerExchange exchange) {
        ServletRequestContext context = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        return (HttpServletRequest) context.getServletRequest();
    }

    /**
     * Extracts the HTTP servlet response from the HTTP server exchange.
     *
     * @param exchange the HTTP server exchange.
     * @return the HTTP servlet response.
     */
    private static HttpServletResponse extractHttpServletResponse(HttpServerExchange exchange) {
        ServletRequestContext context = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        return (HttpServletResponse) context.getServletResponse();
    }

    /**
     * The server adapter.
     */
    private static class ServerAdapter implements ch.qos.logback.access.spi.ServerAdapter {

        /**
         * The HTTP server exchange.
         */
        private final HttpServerExchange exchange;

        /**
         * Constructs an instance.
         *
         * @param exchange the HTTP server exchange.
         */
        private ServerAdapter(HttpServerExchange exchange) {
            this.exchange = exchange;
        }

        /** {@inheritDoc} */
        @Override
        public long getRequestTimestamp() {
            long currentTimeMillis = System.currentTimeMillis();
            long nanoTime = System.nanoTime();
            long requestStartTime = exchange.getRequestStartTime();
            return currentTimeMillis - TimeUnit.NANOSECONDS.toMillis(nanoTime - requestStartTime);
        }

        /** {@inheritDoc} */
        @Override
        public int getStatusCode() {
            return exchange.getStatusCode();
        }

        /** {@inheritDoc} */
        @Override
        public long getContentLength() {
            return exchange.getResponseBytesSent();
        }

        /** {@inheritDoc} */
        @Override
        public Map<String, String> buildResponseHeaderMap() {
            Map<String, String> result = new HashMap<>();
            HeaderMap headers = exchange.getResponseHeaders();
            for (HeaderValues header : headers) {
                result.put(header.getHeaderName().toString(), header.getFirst());
            }
            return result;
        }

    }

}
