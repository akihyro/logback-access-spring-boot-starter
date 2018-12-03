package net.rakugakibox.spring.boot.logback.access.test.queue;

import net.rakugakibox.spring.boot.logback.access.LogbackAccessAppendedEvent;
import net.rakugakibox.spring.boot.logback.access.LogbackAccessDeniedEvent;
import org.springframework.context.event.EventListener;

/**
 * The Logback-access listener that pushes to static {@link LogbackAccessEventQueue}.
 */
public class LogbackAccessEventQueuingListener {

    /**
     * The queue of appended Logback-access event.
     */
    public static final LogbackAccessEventQueue appendedEventQueue = new LogbackAccessEventQueue();

    /**
     * The queue of denied Logback-access event.
     */
    public static final LogbackAccessEventQueue deniedEventQueue = new LogbackAccessEventQueue();

    /**
     * Pushes appended Logback-access event to the queue.
     *
     * @param event an application event indicating that Logback-access event was appended.
     */
    @EventListener
    public void onAppended(LogbackAccessAppendedEvent event) {
        appendedEventQueue.push(event.getEvent());
    }

    /**
     * Pushes denied Logback-access event to the queue.
     *
     * @param event an application event indicating that Logback-access event was denied.
     */
    @EventListener
    public void onDenied(LogbackAccessDeniedEvent event) {
        deniedEventQueue.push(event.getEvent());
    }

}
