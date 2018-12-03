package net.rakugakibox.spring.boot.logback.access;

import ch.qos.logback.access.spi.IAccessEvent;
import net.rakugakibox.spring.boot.logback.access.test.AbstractWebContainerTest;
import net.rakugakibox.spring.boot.logback.access.test.ContainerType;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingAppender;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingAppenderRule;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingListener;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingListenerRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static net.rakugakibox.spring.boot.logback.access.test.asserts.AccessEventAssert.assertThat;
import static net.rakugakibox.spring.boot.logback.access.test.asserts.ResponseEntityAssert.assertThat;

/**
 * The base class for testing Spring Security attributes.
 */
@RunWith(Parameterized.class)
@SpringBootTest(
        value = {
                "spring.security.user.name=test-user",
                "spring.security.user.password=TEST-PASSWORD",
                "logback.access.config=classpath:logback-access.queue.xml",
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class SecurityAttributesTest extends AbstractWebContainerTest {

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<?> data() {
        // No security support for reactive types yet
        return ContainerType.servlet();
    }

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
     * Tests a Logback-access event.
     */
    @Test
    public void logbackAccessEvent() {

        ResponseEntity<String> response = rest
                .withBasicAuth("TEST-USER", "TEST-PASSWORD")
                .getForEntity("/test/text", String.class);
        IAccessEvent event = LogbackAccessEventQueuingAppender.appendedEventQueue.pop();
        LogbackAccessEventQueuingListener.appendedEventQueue.pop();

        assertThat(response).hasStatusCode(HttpStatus.OK);
        assertThat(event).hasRemoteUser("test-user");

    }

}
