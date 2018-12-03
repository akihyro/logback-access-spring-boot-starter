package net.rakugakibox.spring.boot.logback.access.test;

import net.rakugakibox.spring.boot.logback.access.test.config.LogbackAccessEventQueuingListenerConfiguration;
import net.rakugakibox.spring.boot.logback.access.test.config.TestControllerConfiguration;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Parameterized.UseParametersRunnerFactory(SpringParametersRunnerFactory.class)
@Import({ LogbackAccessEventQueuingListenerConfiguration.class, TestControllerConfiguration.class })
public class AbstractWebContainerTest {

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<?> data() {
        return ContainerType.all();
    }

    /**
     * The server port.
     */
    @LocalServerPort
    protected int port;

    /**
     * The indicator for current running context.
     * Can be useful for debugging.
     *
     * Must be public according to @Parameter specification.
     */
    @Parameterized.Parameter
    public ContainerType containerType;

    /**
     * The REST template.
     */
    @Autowired
    protected TestRestTemplate rest;

}
