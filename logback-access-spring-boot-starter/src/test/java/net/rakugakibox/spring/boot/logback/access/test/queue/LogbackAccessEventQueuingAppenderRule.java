package net.rakugakibox.spring.boot.logback.access.test.queue;

import lombok.extern.slf4j.Slf4j;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The test rule for using {@link LogbackAccessEventQueuingAppender}.
 */
@Slf4j
public class LogbackAccessEventQueuingAppenderRule extends TestWatcher {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void starting(Description description) {
        initializeLogbackAccessEventQueues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void succeeded(Description description) {
        verifyLogbackAccessEventQueues();
    }

    /**
     * Initializes the queues of {@link LogbackAccessEventQueuingAppender}.
     */
    private void initializeLogbackAccessEventQueues() {
        LogbackAccessEventQueuingAppender.appendedEventQueue.clear();
        log.debug("Initialized the queues of LogbackAccessEventQueuingAppender");
    }

    /**
     * Verifies the queues of {@link LogbackAccessEventQueuingAppender}.
     */
    private void verifyLogbackAccessEventQueues() {
        assertThat(LogbackAccessEventQueuingAppender.appendedEventQueue.isEmpty())
                .as("Verifies that the LogbackAccessEventQueuingAppender.appendedEventQueue is empty")
                .isTrue();
        log.debug("Verifyed the queues of LogbackAccessEventQueuingAppender");
    }

}
