package net.rakugakibox.spring.boot.logback.access.test.queue;

import lombok.extern.slf4j.Slf4j;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The test rule for using {@link LogbackAccessEventQueuingListener}.
 */
@Slf4j
public class LogbackAccessEventQueuingListenerRule extends TestWatcher {

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
     * Initializes the queues of {@link LogbackAccessEventQueuingListener}.
     */
    private void initializeLogbackAccessEventQueues() {
        LogbackAccessEventQueuingListener.appendedEventQueue.clear();
        LogbackAccessEventQueuingListener.deniedEventQueue.clear();
        log.debug("Initialized the queues of LogbackAccessEventQueuingListener");
    }

    /**
     * Verifies the queues of {@link LogbackAccessEventQueuingListener}.
     */
    private void verifyLogbackAccessEventQueues() {
        assertThat(LogbackAccessEventQueuingListener.appendedEventQueue.isEmpty())
                .as("Verifies that the LogbackAccessEventQueuingListener.appendedEventQueue is empty")
                .isTrue();
        assertThat(LogbackAccessEventQueuingListener.deniedEventQueue.isEmpty())
                .as("Verifies that the LogbackAccessEventQueuingListener.deniedEventQueue is empty")
                .isTrue();
        log.debug("Verifyed the queues of LogbackAccessEventQueuingListener");
    }

}
