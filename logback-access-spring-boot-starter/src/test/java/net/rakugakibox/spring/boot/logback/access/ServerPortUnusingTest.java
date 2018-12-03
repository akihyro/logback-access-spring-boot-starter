package net.rakugakibox.spring.boot.logback.access;

import ch.qos.logback.access.spi.IAccessEvent;
import net.rakugakibox.spring.boot.logback.access.test.AbstractWebContainerTest;
import net.rakugakibox.spring.boot.logback.access.test.asserts.AccessEventAssert;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingAppender;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingAppenderRule;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingListener;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingListenerRule;
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
 * The base class for testing to unuse the server port.
 */
@RunWith(Parameterized.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@SpringBootTest(
        value = {
                "server.useForwardHeaders=true",
                "logback.access.config=classpath:logback-access.queue.xml",
                "logback.access.useServerPortInsteadOfLocalPort=false",
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ServerPortUnusingTest extends AbstractWebContainerTest {

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

    @Test
    public void should_process_request_with_forwarded_host_and_port() {

        Assume.assumeTrue(!containerType.isUndertow());

        RequestEntity<Void> request = RequestEntity
                .get(rest.getRestTemplate().getUriTemplateHandler().expand("/test/text"))
                .header("X-Forwarded-Host", "forwarded-host")
                .header("X-Forwarded-Port", "12345")
                .build();
        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = LogbackAccessEventQueuingAppender.appendedEventQueue.pop();
        LogbackAccessEventQueuingListener.appendedEventQueue.pop();

        assertThat(response).hasStatusCode(HttpStatus.OK);
        AccessEventAssert.assertThat(event).hasLocalPort(port);

    }

    @Test
    public void should_process_request_with_forwarded_host() {

        Assume.assumeTrue(!containerType.isUndertow());

        RequestEntity<Void> request = RequestEntity
                .get(rest.getRestTemplate().getUriTemplateHandler().expand("/test/text"))
                .header("X-Forwarded-Host", "forwarded-host:12345")
                .build();
        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = LogbackAccessEventQueuingAppender.appendedEventQueue.pop();
        LogbackAccessEventQueuingListener.appendedEventQueue.pop();

        assertThat(response).hasStatusCode(HttpStatus.OK);
        AccessEventAssert.assertThat(event).hasLocalPort(port);

    }

    @Test
    public void should_process_request_with_forwarded_port() {

        RequestEntity<Void> request = RequestEntity
                .get(rest.getRestTemplate().getUriTemplateHandler().expand("/test/text"))
                .header("X-Forwarded-Port", "12345")
                .build();
        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = LogbackAccessEventQueuingAppender.appendedEventQueue.pop();
        LogbackAccessEventQueuingListener.appendedEventQueue.pop();

        assertThat(response).hasStatusCode(HttpStatus.OK);
        AccessEventAssert.assertThat(event).hasLocalPort(port);

    }

}
