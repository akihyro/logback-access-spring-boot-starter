package net.rakugakibox.spring.boot.logback.access;

import ch.qos.logback.access.spi.IAccessEvent;
import net.rakugakibox.spring.boot.logback.access.test.AbstractWebContainerTest;
import net.rakugakibox.spring.boot.logback.access.test.asserts.AccessEventAssert;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingAppender;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingAppenderRule;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingListener;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingListenerRule;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static net.rakugakibox.spring.boot.logback.access.test.asserts.ResponseEntityAssert.assertThat;

/**
 * The base class for testing to use {@code X-Forwarded-*} headers.
 */
@RunWith(Parameterized.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@SpringBootTest(
        value = {
                "server.useForwardHeaders=true",
                "logback.access.config=classpath:logback-access.queue.xml",
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ForwardHeadersUsingTest extends AbstractWebContainerTest {

    /**
     * Creates a test rule.
     *
     * @return a test rule.
     */
    @Rule
    public TestRule rule() {
        return RuleChain
                .outerRule(new LogbackAccessEventQueuingAppenderRule())
                .around(new LogbackAccessEventQueuingListenerRule());
    }

    /**
     * Tomcat: No support for X-Forwarded-Host
     *
     * Jetty: No support for X-Forwarded-Port
     * Jetty {@link ForwardedRequestCustomizer} implements rfc7239 in which there is no mention about X-Forwarded-Port
     */
    @Test
    public void should_process_request_with_forwarded_host_and_port() {

        Assume.assumeTrue(!containerType.isJetty() && !containerType.isTomcat());

        RequestEntity<Void> request = RequestEntity
                .get(rest.getRestTemplate().getUriTemplateHandler().expand("/test/text"))
                .header("X-Forwarded-Host", "forwarded-host")
                .header("X-Forwarded-Port", "12345")
                .header("X-Forwarded-For", "1.2.3.4")
                .header("X-Forwarded-Proto", "https")
                .build();
        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = LogbackAccessEventQueuingAppender.appendedEventQueue.pop();
        LogbackAccessEventQueuingListener.appendedEventQueue.pop();

        assertThat(response).hasStatusCode(HttpStatus.OK);
        AccessEventAssert.assertThat(event)
                .hasServerName("forwarded-host")
                .hasLocalPort(12345)
                .hasRemoteAddr("1.2.3.4")
                .hasRemoteHost("1.2.3.4")
                .hasProtocol("HTTP/1.1");

    }

    /**
     * Tomcat: No support for X-Forwarded-Port
     */
    @Test
    public void should_process_request_with_forwarded_host() {

        Assume.assumeTrue(!containerType.isTomcat());

        RequestEntity<Void> request = RequestEntity
                .get(rest.getRestTemplate().getUriTemplateHandler().expand("/test/text"))
                .header("X-Forwarded-Host", "forwarded-host:12345")
                .header("X-Forwarded-For", "1.2.3.4")
                .header("X-Forwarded-Proto", "https")
                .build();
        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = LogbackAccessEventQueuingAppender.appendedEventQueue.pop();
        LogbackAccessEventQueuingListener.appendedEventQueue.pop();

        assertThat(response).hasStatusCode(HttpStatus.OK);
        AccessEventAssert.assertThat(event)
                .hasServerName("forwarded-host")
                .hasLocalPort(12345)
                .hasRemoteAddr("1.2.3.4")
                .hasRemoteHost("1.2.3.4")
                .hasProtocol("HTTP/1.1");

    }

    /**
     * Jetty: No support for X-Forwarded-Port
     *
     * Undertow handler {@link io.undertow.server.handlers.ProxyPeerAddressHandler} processes X-Forwarded-Port
     * only in combination with X-Forwarded-Host header
     */
    @Test
    public void should_process_request_with_forwarded_port() {

        Assume.assumeTrue(!containerType.isJetty() && !containerType.isUndertow());

        RequestEntity<Void> request = RequestEntity
                .get(rest.getRestTemplate().getUriTemplateHandler().expand("/test/text"))
                .header("X-Forwarded-Port", "12345")
                .header("X-Forwarded-For", "1.2.3.4")
                .header("X-Forwarded-Proto", "https")
                .build();
        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = LogbackAccessEventQueuingAppender.appendedEventQueue.pop();
        LogbackAccessEventQueuingListener.appendedEventQueue.pop();

        assertThat(response).hasStatusCode(HttpStatus.OK);
        AccessEventAssert.assertThat(event)
                .hasServerName("localhost")
                .hasLocalPort(12345)
                .hasRemoteAddr("1.2.3.4")
                .hasRemoteHost("1.2.3.4")
                .hasProtocol("HTTP/1.1");

    }

}
