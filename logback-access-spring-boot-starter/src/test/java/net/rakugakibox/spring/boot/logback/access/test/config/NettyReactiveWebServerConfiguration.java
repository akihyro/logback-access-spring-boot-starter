package net.rakugakibox.spring.boot.logback.access.test.config;

import net.rakugakibox.spring.boot.logback.access.test.ContainerType;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.SocketUtils;

/**
 * The configuration for using {@link NettyReactiveWebServerFactory}.
 */
@Configuration
@Import(GenericReactiveWebServerConfiguration.class)
public class NettyReactiveWebServerConfiguration {

    /**
     * Creates a {@link NettyReactiveWebServerFactory}.
     *
     * @return a {@link NettyReactiveWebServerFactory}.
     */
    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        return new NettyReactiveWebServerFactory();
    }

    @Bean
    public TestRestTemplate testRestTemplate(PortCustomizer customizer) {
        RestTemplateBuilder builder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + customizer.availableTcpPort);

        return new TestRestTemplate(builder);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableReactiveWebServerFactory> portCustomizer() {
        return new PortCustomizer(SocketUtils.findAvailableTcpPort());
    }

    @Bean
    public ContainerType containerType() {
        return ContainerType.REACTOR_NETTY;
    }

    private static class PortCustomizer implements WebServerFactoryCustomizer<ConfigurableReactiveWebServerFactory> {

        private int availableTcpPort;

        PortCustomizer(int availableTcpPort) {
            this.availableTcpPort = availableTcpPort;
        }

        @Override
        public void customize(ConfigurableReactiveWebServerFactory factory) {
            factory.setPort(availableTcpPort);
        }
    }

}

