package net.rakugakibox.spring.boot.logback.access.test.queue;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * The Logback-access appender that pushes to static {@link LogbackAccessEventQueue}.
 */
public class LogbackAccessEventQueuingAppender extends AppenderBase<IAccessEvent> {

    /**
     * The queue of appended Logback-access event.
     */
    public static final LogbackAccessEventQueue appendedEventQueue = new LogbackAccessEventQueue();

    /** {@inheritDoc} */
    @Override
    protected void append(IAccessEvent event) {
        appendedEventQueue.push(event);
    }

}
