package net.rakugakibox.spring.boot.logback.access;

import net.rakugakibox.spring.boot.logback.access.test.AbstractWebContainerTest;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingAppenderRule;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingListener;
import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingListenerRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static net.rakugakibox.spring.boot.logback.access.test.asserts.ResponseEntityAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The base class for testing to use {@code <springProperty>} tag.
 */
@RunWith(Parameterized.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@SpringBootTest(
        value = {
                "logback.access.config=classpath:logback-access.propertied.xml",
                "logback.access.test.console.pattern.prefix=>>>",
                "logback.access.test.console.pattern.suffix=<<<",
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class SpringPropertyTest extends AbstractWebContainerTest {

    /**
     * The output capture rule.
     */
    private final OutputCapture outputCapture = new OutputCapture();

    /**
     * The REST template.
     */
    @Autowired
    protected TestRestTemplate rest;

    /**
     * Creates a test rule.
     *
     * @return a test rule.
     */
    @Rule
    public TestRule rule() {
        return RuleChain
                .outerRule(new LogbackAccessEventQueuingAppenderRule())
                .around(new LogbackAccessEventQueuingListenerRule())
                .around(outputCapture);
    }

    /**
     * Tests a Logback-access event.
     */
    @Test
    public void should_output_log() {

        ResponseEntity<String> response = rest.getForEntity("/test/text", String.class);
        LogbackAccessEventQueuingListener.appendedEventQueue.pop();

        assertThat(response).hasStatusCode(HttpStatus.OK);
        assertThat(outputCapture.toString())
                .containsSequence(">>>", "127.0.0.1", "GET", "/test/text", "HTTP/1.1", "200", "<<<");

    }

}
