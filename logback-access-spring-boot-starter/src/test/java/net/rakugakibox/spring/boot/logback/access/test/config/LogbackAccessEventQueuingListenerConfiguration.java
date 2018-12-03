package net.rakugakibox.spring.boot.logback.access.test.config;

import net.rakugakibox.spring.boot.logback.access.test.queue.LogbackAccessEventQueuingListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration for using {@link LogbackAccessEventQueuingListener}.
 */
@Configuration
public class LogbackAccessEventQueuingListenerConfiguration {

    /**
     * Creates a {@link LogbackAccessEventQueuingListener}.
     *
     * @return a {@link LogbackAccessEventQueuingListener}.
     */
    @Bean
    public LogbackAccessEventQueuingListener logbackAccessEventQueuingListener() {
        return new LogbackAccessEventQueuingListener();
    }

}
