package net.rakugakibox.spring.boot.logback.access;

import ch.qos.logback.core.spi.FilterReply;
import net.rakugakibox.spring.boot.logback.access.test.AbstractWebContainerTest;
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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static net.rakugakibox.spring.boot.logback.access.test.asserts.ResponseEntityAssert.assertThat;

/**
 * Class for testing to filter Logback-access events.
 */
@RunWith(Parameterized.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@SpringBootTest(
        value = "logback.access.config=classpath:logback-access.filtered.queue.xml",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class LogbackAccessFilteringTest extends AbstractWebContainerTest {

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
     * Tests an accepted Logback-access event.
     */
    @Test
    public void acceptedLogbackAccessEvent() {

        RequestEntity<Void> request = RequestEntity
                .get(rest.getRestTemplate().getUriTemplateHandler().expand("/test/text"))
                .header("X-Filter-Reply", FilterReply.ACCEPT.name())
                .build();
        ResponseEntity<String> response = rest.exchange(request, String.class);
        assertThat(response).hasStatusCode(HttpStatus.OK);

        LogbackAccessEventQueuingAppender.appendedEventQueue.pop();
        LogbackAccessEventQueuingListener.appendedEventQueue.pop();

    }

    /**
     * Tests an neutral Logback-access event.
     */
    @Test
    public void neutralLogbackAccessEvent() {

        RequestEntity<Void> request = RequestEntity
                .get(rest.getRestTemplate().getUriTemplateHandler().expand("/test/text"))
                .header("X-Filter-Reply", FilterReply.NEUTRAL.name())
                .build();
        ResponseEntity<String> response = rest.exchange(request, String.class);
        assertThat(response).hasStatusCode(HttpStatus.OK);

        LogbackAccessEventQueuingAppender.appendedEventQueue.pop();
        LogbackAccessEventQueuingListener.appendedEventQueue.pop();

    }

    /**
     * Tests a denied Logback-access event.
     */
    @Test
    public void deniedLogbackAccessEvent() {

        RequestEntity<Void> request = RequestEntity
                .get(rest.getRestTemplate().getUriTemplateHandler().expand("/test/text"))
                .header("X-Filter-Reply", FilterReply.DENY.name())
                .build();
        ResponseEntity<String> response = rest.exchange(request, String.class);
        assertThat(response).hasStatusCode(HttpStatus.OK);

        LogbackAccessEventQueuingListener.deniedEventQueue.pop();
    }
}
