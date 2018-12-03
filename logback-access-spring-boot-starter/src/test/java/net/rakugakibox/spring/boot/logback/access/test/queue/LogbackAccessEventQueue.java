package net.rakugakibox.spring.boot.logback.access.test.queue;

import ch.qos.logback.access.spi.IAccessEvent;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.SerializationUtils.deserialize;
import static org.springframework.util.SerializationUtils.serialize;

/**
 * The queue of Logback-access event.
 * This class is thread safe.
 */
public class LogbackAccessEventQueue {

    /**
     * The timeout for accessing the queue.
     */
    public static final Duration TIMEOUT_FOR_QUEUE_ACCESS = Duration.ofMinutes(1L);

    /**
     * The queue.
     */
    private final BlockingQueue<IAccessEvent> queue = new LinkedBlockingQueue<>();

    /**
     * Adds the Logback-access event.
     * Waits a moment for space becomes available if necessary.
     * If it times out, throws an exception.
     *
     * @param event the Logback-access event.
     */
    public void push(IAccessEvent event) {
        event.prepareForDeferredProcessing();
        event = (IAccessEvent) deserialize(serialize(event));
        try {
            if (!queue.offer(event, TIMEOUT_FOR_QUEUE_ACCESS.getSeconds(), TimeUnit.SECONDS)) {
                throw new IllegalStateException("Could not push a Logback-access event: event=[" + event + "]");
            }
        } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Could not push a Logback-access event: event=[" + event + "]", exc);
        }
    }

    /**
     * Removes and returns a head Logback-access event.
     * Waits a moment for a Logback-access event becomes available if necessary.
     * If it times out, throws an exception.
     *
     * @return a Logback-access event.
     */
    public IAccessEvent pop() {
        try {
            IAccessEvent event = queue.poll(TIMEOUT_FOR_QUEUE_ACCESS.getSeconds(), TimeUnit.SECONDS);
            if (event == null) {
                throw new IllegalStateException("Could not pop a Logback-access event");
            }
            return event;
        } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Could not pop a Logback-access event", exc);
        }
    }

    /**
     * Returns whether there is no Logback-access events.
     *
     * @return {@code true} if there is no Logback-access events, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Removes all Logback-access events.
     */
    public void clear() {
        queue.clear();
    }

}
