package net.rakugakibox.spring.boot.logback.access.test.asserts;

import ch.qos.logback.access.spi.IAccessEvent;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static ch.qos.logback.access.spi.IAccessEvent.NA;
import static java.util.Collections.list;

/**
 * The assertion of {@link IAccessEvent}.
 *
 * @param <S> the type of self.
 * @param <A> the type of actual value.
 */
public class AccessEventAssert<S extends AccessEventAssert<S, A>, A extends IAccessEvent> extends AbstractAssert<S, A> {

    /**
     * Constructs an instance.
     *
     * @param actual the actual value.
     */
    protected AccessEventAssert(A actual) {
        super(actual, AccessEventAssert.class);
    }

    /**
     * Constructs an instance.
     *
     * @param actual the actual value.
     * @param selfClass the self class.
     */
    protected AccessEventAssert(A actual, Class<?> selfClass) {
        super(actual, selfClass);
    }

    /**
     * Verifies that the timestamp is in given range.
     *
     * @param start the start value of range (inclusive).
     * @param end the end value of range (inclusive).
     * @return this instance.
     * @see IAccessEvent#getTimeStamp()
     */
    public S hasTimestamp(LocalDateTime start, LocalDateTime end) {
        long actualTimestampAsLong = actual.getTimeStamp();
        LocalDateTime actualTimestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(actualTimestampAsLong), ZoneId.systemDefault());
        Assertions.assertThat(actualTimestamp)
                .isAfterOrEqualTo(start)
                .isBeforeOrEqualTo(end);
        return myself;
    }

    /**
     * Verifies that the server name is equal to the given one.
     *
     * @param serverName the server name.
     * @return this instance.
     * @see IAccessEvent#getServerName()
     */
    public S hasServerName(String serverName) {
        String actualServerName = actual.getServerName();
        Assertions.assertThat(actualServerName).isEqualTo(serverName);
        return myself;
    }

    /**
     * Verifies that the local port is equal to the given one.
     *
     * @param localPort the local port.
     * @return this instance.
     * @see IAccessEvent#getLocalPort()
     */
    public S hasLocalPort(int localPort) {
        int actualLocalPort = actual.getLocalPort();
        Assertions.assertThat(actualLocalPort).isEqualTo(localPort);
        return myself;
    }

    /**
     * Verifies that the remote address is equal to the given one.
     *
     * @param remoteAddr the remote address.
     * @return this instance.
     * @see IAccessEvent#getRemoteAddr()
     */
    public S hasRemoteAddr(String remoteAddr) {
        String actualRemoteAddr = actual.getRemoteAddr();
        Assertions.assertThat(actualRemoteAddr).isEqualTo(remoteAddr);
        return myself;
    }

    /**
     * Verifies that the remote host is equal to the given one.
     *
     * @param remoteHost the remote host.
     * @return this instance.
     * @see IAccessEvent#getRemoteHost()
     */
    public S hasRemoteHost(String remoteHost) {
        String actualRemoteHost = actual.getRemoteHost();
        Assertions.assertThat(actualRemoteHost).isEqualTo(remoteHost);
        return myself;
    }

    /**
     * Verifies that the remote user is equal to the given one.
     *
     * @param remoteUser the remote user.
     * @return this instance.
     * @see IAccessEvent#getRemoteUser()
     */
    public S hasRemoteUser(String remoteUser) {
        String actualRemoteUser = actual.getRemoteUser();
        Assertions.assertThat(actualRemoteUser).isEqualTo(remoteUser);
        return myself;
    }

    /**
     * Verifies that the remote user is N/A.
     *
     * @return this instance.
     * @see IAccessEvent#getRemoteUser()
     */
    public S doesNotHaveRemoteUser() {
        String actualRemoteUser = actual.getRemoteUser();
        Assertions.assertThat(actualRemoteUser).isEqualTo(NA);
        return myself;
    }

    /**
     * Verifies that the protocol is equal to the given one.
     *
     * @param protocol the protocol.
     * @return this instance.
     * @see IAccessEvent#getProtocol()
     */
    public S hasProtocol(String protocol) {
        String actualProtocol = actual.getProtocol();
        Assertions.assertThat(actualProtocol).isEqualTo(protocol);
        return myself;
    }

    /**
     * Verifies that the method is equal to the given one.
     *
     * @param method the method.
     * @return this instance.
     * @see IAccessEvent#getMethod()
     */
    public S hasMethod(HttpMethod method) {
        String actualMethod = actual.getMethod();
        Assertions.assertThat(actualMethod).isEqualTo(method.name());
        return myself;
    }

    /**
     * Verifies that the request URI is equal to the given one.
     *
     * @param requestUri the request URI.
     * @return this instance.
     * @see IAccessEvent#getRequestURI()
     */
    public S hasRequestUri(String requestUri) {
        String actualRequestUri = actual.getRequestURI();
        Assertions.assertThat(actualRequestUri).isEqualTo(requestUri);
        return myself;
    }

    /**
     * Verifies that the query string is equal to the given one.
     *
     * @param queryString the query string.
     * @return this instance.
     * @see IAccessEvent#getQueryString()
     */
    public S hasQueryString(String queryString) {
        String actualQueryString = actual.getQueryString();
        Assertions.assertThat(actualQueryString).isEqualTo(queryString);
        return myself;
    }

    /**
     * Verifies that the request URL (the first line of the request) is equal to the given one.
     *
     * @param method the method.
     * @param requestUri the request URI.
     * @param protocol the protocol.
     * @return this instance.
     * @see IAccessEvent#getRequestURL()
     */
    public S hasRequestUrl(HttpMethod method, String requestUri, String protocol) {
        String actualRequestUrl = actual.getRequestURL();
        Assertions.assertThat(actualRequestUrl).isEqualTo(method.name() + " " + requestUri + " " + protocol);
        return myself;
    }

    /**
     * Verifies that the request header names contains the given one.
     *
     * @param name the request header name.
     * @return this instance.
     * @see IAccessEvent#getRequestHeaderNames()
     */
    public S hasRequestHeaderName(String name) {
        Enumeration<String> actualRequestHeaderNamesAsEnumeration = actual.getRequestHeaderNames();
        List<String> actualRequestHeaderNames = list(actualRequestHeaderNamesAsEnumeration);
        Assertions.assertThat(actualRequestHeaderNames)
                .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
                .contains(name);
        return myself;
    }

    /**
     * Verifies that the request header names does not have contain the given one.
     *
     * @param name the request header name.
     * @return this instance.
     * @see IAccessEvent#getRequestHeaderNames()
     */
    public S doesNotHaveRequestHeaderName(String name) {
        Enumeration<String> actualRequestHeaderNamesAsEnumeration = actual.getRequestHeaderNames();
        List<String> actualRequestHeaderNames = list(actualRequestHeaderNamesAsEnumeration);
        Assertions.assertThat(actualRequestHeaderNames)
                .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
                .doesNotContain(name);
        return myself;
    }

    /**
     * Verifies that the request headers contains the given one.
     *
     * @param name the request header name.
     * @param value the request header value.
     * @return this instance.
     * @see IAccessEvent#getRequestHeader(String)
     */
    public S hasRequestHeader(String name, String value) {
        String actualRequestHeader = actual.getRequestHeader(name);
        Assertions.assertThat(actualRequestHeader).isEqualTo(value);
        return myself;
    }

    /**
     * Verifies that the request headers does not have contain the given one.
     *
     * @param name the request header name.
     * @return this instance.
     * @see IAccessEvent#getRequestHeader(String)
     */
    public S doesNotHaveRequestHeader(String name) {
        String actualRequestHeader = actual.getRequestHeader(name);
        Assertions.assertThat(actualRequestHeader).isEqualTo(NA);
        return myself;
    }

    /**
     * Verifies that the request header map contains the given one.
     *
     * @param name the request header name.
     * @param value the request header value.
     * @return this instance.
     * @see IAccessEvent#getRequestHeaderMap()
     */
    public S hasRequestHeaderInMap(String name, String value) {
        Map<String, String> actualRequestHeaders = actual.getRequestHeaderMap();
        Assertions.assertThat(actualRequestHeaders).containsEntry(name, value);
        return myself;
    }

    /**
     * Verifies that the request header map does not have contain the given one.
     *
     * @param name the request header name.
     * @return this instance.
     * @see IAccessEvent#getRequestHeaderMap()
     */
    public S doesNotHaveRequestHeaderInMap(String name) {
        Map<String, String> actualRequestHeaders = actual.getRequestHeaderMap();
        Assertions.assertThat(actualRequestHeaders).doesNotContainKey(name);
        return myself;
    }

    /**
     * Verifies that the request parameters contains the given one.
     *
     * @param name the request parameter name.
     * @param values the request parameter values.
     * @return this instance.
     * @see IAccessEvent#getRequestParameter(String)
     */
    public S hasRequestParameter(String name, String... values) {
        String[] actualRequestParameter = actual.getRequestParameter(name);
        Assertions.assertThat(actualRequestParameter).containsExactlyInAnyOrder(values);
        return myself;
    }

    /**
     * Verifies that the request parameters does not contain the given one.
     *
     * @param name the request parameter name.
     * @return this instance.
     * @see IAccessEvent#getRequestParameter(String)
     */
    public S doesNotHaveRequestParameter(String name) {
        String[] actualRequestParameter = actual.getRequestParameter(name);
        Assertions.assertThat(actualRequestParameter).containsExactlyInAnyOrder(NA);
        return myself;
    }

    /**
     * Verifies that the request parameter map contains the given one.
     *
     * @param name the request parameter name.
     * @param values the request parameter values.
     * @return this instance.
     * @see IAccessEvent#getRequestParameterMap()
     */
    public S hasRequestParameterInMap(String name, String... values) {
        Map<String, String[]> actualRequestParameters = actual.getRequestParameterMap();
        Assertions.assertThat(actualRequestParameters).containsEntry(name, values);
        return myself;
    }

    /**
     * Verifies that the request parameter map does not contain the given one.
     *
     * @param name the request parameter name.
     * @return this instance.
     * @see IAccessEvent#getRequestParameterMap()
     */
    public S doesNotHaveRequestParameterInMap(String name) {
        Map<String, String[]> actualRequestParameters = actual.getRequestParameterMap();
        Assertions.assertThat(actualRequestParameters).doesNotContainKey(name);
        return myself;
    }

    /**
     * Verifies that the status code is equal to the given one.
     *
     * @param status the status.
     * @return this instance.
     * @see IAccessEvent#getStatusCode()
     */
    public S hasStatusCode(HttpStatus status) {
        int actualStatusCode = actual.getStatusCode();
        Assertions.assertThat(actualStatusCode).isEqualTo(status.value());
        return myself;
    }

    /**
     * Verifies that the content length is greater than or equal to the given one.
     *
     * @param contentLength the minimal content length.
     * @return this instance.
     * @see IAccessEvent#getContentLength()
     */
    public S hasContentLength(long contentLength) {
        long actualContentLength = actual.getContentLength();
        Assertions.assertThat(actualContentLength).isGreaterThanOrEqualTo(contentLength);
        return myself;
    }

    /**
     * Verifies that the response header names contains the given one.
     *
     * @param name the response header name.
     * @return this instance.
     * @see IAccessEvent#getResponseHeaderNameList()
     */
    public S hasResponseHeaderName(String name) {
        List<String> actualResponseHeaderNames = actual.getResponseHeaderNameList();
        Assertions.assertThat(actualResponseHeaderNames)
                .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
                .contains(name);
        return myself;
    }

    /**
     * Verifies that the response header names does not have contain the given one.
     *
     * @param name the response header name.
     * @return this instance.
     * @see IAccessEvent#getResponseHeaderNameList()
     */
    public S doesNotHaveResponseHeaderName(String name) {
        List<String> actualResponseHeaderNames = actual.getResponseHeaderNameList();
        Assertions.assertThat(actualResponseHeaderNames)
                .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
                .doesNotContain(name);
        return myself;
    }

    /**
     * Verifies that the response headers contains the given one.
     *
     * @param name the response header name.
     * @param value the response header value.
     * @return this instance.
     * @see IAccessEvent#getResponseHeader(String)
     */
    public S hasResponseHeader(String name, String value) {
        String actualResponseHeader = actual.getResponseHeader(name);
        Assertions.assertThat(actualResponseHeader).isEqualTo(value);
        return myself;
    }

    /**
     * Verifies that the response headers does not have contain the given one.
     *
     * @param name the response header name.
     * @return this instance.
     * @see IAccessEvent#getResponseHeader(String)
     */
    public S doesNotHaveResponseHeader(String name) {
        String actualResponseHeader = actual.getResponseHeader(name);
        Assertions.assertThat(actualResponseHeader).isNull();
        return myself;
    }

    /**
     * Verifies that the response header map contains the given one.
     *
     * @param name the response header name.
     * @param value the response header value.
     * @return this instance.
     * @see IAccessEvent#getResponseHeaderMap()
     */
    public S hasResponseHeaderInMap(String name, String value) {
        Map<String, String> actualResponseHeaders = actual.getResponseHeaderMap();
        Assertions.assertThat(actualResponseHeaders).containsEntry(name, value);
        return myself;
    }

    /**
     * Verifies that the response header map does not have contain the given one.
     *
     * @param name the response header name.
     * @return this instance.
     * @see IAccessEvent#getResponseHeaderMap()
     */
    public S doesNotHaveResponseHeaderInMap(String name) {
        Map<String, String> actualResponseHeaders = actual.getResponseHeaderMap();
        Assertions.assertThat(actualResponseHeaders).doesNotContainKey(name);
        return myself;
    }

    /**
     * Verifies that the elapsed time is in given range.
     *
     * @param start the start value of range (inclusive).
     * @param end the end value of range (exclusive).
     * @return this instance.
     * @see IAccessEvent#getElapsedTime()
     */
    public S hasElapsedTime(LocalDateTime start, LocalDateTime end) {
        long actualElapsedTimeAsLong = actual.getElapsedTime();
        Duration actualElapsedTime = Duration.ofMillis(actualElapsedTimeAsLong);
        Assertions.assertThat(actualElapsedTime)
                .isGreaterThanOrEqualTo(Duration.ofMillis(0L))
                .isLessThanOrEqualTo(Duration.between(start, end));
        return myself;
    }

    /**
     * Verifies that the elapsed seconds is in given range.
     *
     * @param start the start value of range (inclusive).
     * @param end the end value of range (exclusive).
     * @return this instance.
     * @see IAccessEvent#getElapsedSeconds()
     */
    public S hasElapsedSeconds(LocalDateTime start, LocalDateTime end) {
        long actualElapsedSecondsAsLong = actual.getElapsedSeconds();
        Duration actualElapsedSeconds = Duration.ofSeconds(actualElapsedSecondsAsLong);
        Assertions.assertThat(actualElapsedSeconds)
                .isGreaterThanOrEqualTo(Duration.ofSeconds(0L))
                .isLessThanOrEqualTo(Duration.between(start, end));
        return myself;
    }

    /**
     * Verifies that the thread name is available.
     *
     * @return this instance.
     * @see IAccessEvent#getThreadName()
     */
    public S hasThreadName() {
        String actualThreadName = actual.getThreadName();
        Assertions.assertThat(actualThreadName)
                .isNotEqualTo(IAccessEvent.NA)
                .isNotEmpty();
        return myself;
    }

    /**
     * Creates an assertion.
     *
     * @param <A> the type of actual value.
     * @param actual the actual value.
     * @return an assertion.
     */
    public static <A extends IAccessEvent> AccessEventAssert<?, A> assertThat(A actual) {
        return new AccessEventAssert<>(actual);
    }

}
