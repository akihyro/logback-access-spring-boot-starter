package net.rakugakibox.spring.boot.logback.access.test.config;

import org.reactivestreams.Publisher;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.Callable;

@Configuration
@EnableAutoConfiguration(exclude = { ReactiveSecurityAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class })
public class GenericReactiveWebServerConfiguration implements WebFluxConfigurer {

    @Bean
    public SecurityProperties securityProperties() {
        return new SecurityProperties();
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.customCodecs().encoder(new AbstractEncoder<Callable>(MediaType.TEXT_PLAIN) {
            @Override
            public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
                Class<?> clazz = elementType.toClass();
                return super.canEncode(elementType, mimeType) && Callable.class.isAssignableFrom(clazz);
            }

            @Override
            public Flux<DataBuffer> encode(Publisher<? extends Callable> inputStream,
                                           DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType,
                                           @Nullable Map<String, Object> hints) {

                return Flux.from(inputStream)
                        .map(callable -> {
                            try {
                                return String.valueOf(callable.call()).getBytes();
                            } catch (Exception e) {
                                throw new IllegalStateException(e);
                            }
                        })
                        .map(bytes -> {
                            DataBuffer dataBuffer = bufferFactory.wrap(bytes);
                            if (logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
                                String logPrefix = Hints.getLogPrefix(hints);
                                logger.debug(logPrefix + "Writing " + dataBuffer.readableByteCount() + " bytes");
                            }
                            return dataBuffer;
                        });
            }
        });
    }

}
