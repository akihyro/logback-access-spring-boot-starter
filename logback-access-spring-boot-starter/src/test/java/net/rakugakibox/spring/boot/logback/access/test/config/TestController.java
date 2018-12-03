package net.rakugakibox.spring.boot.logback.access.test.config;

import lombok.RequiredArgsConstructor;
import net.rakugakibox.spring.boot.logback.access.test.ContainerType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.Callable;

import static java.util.Collections.singletonMap;

/**
 * The test controller.
 */
@RequestMapping("/test")
@RestController
@RequiredArgsConstructor
public class TestController {

    private final ContainerType containerType;

    /**
     * Gets the text.
     *
     * @return the text.
     */
    @GetMapping(value = "/text", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getText() {
        return "TEST-TEXT";
    }

    /**
     * Gets the text with header.
     *
     * @return the response entity with header and body.
     */
    @GetMapping(value = "/text-with-header", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getTextWithHeader() {
        return ResponseEntity.ok()
                .header("X-Test-Header", "TEST-HEADER")
                .header("X-Container-Type", containerType.name())
                .body("TEST-TEXT");
    }

    /**
     * Gets the text asynchronously.
     *
     * @return the callback to return the text.
     */
    @GetMapping(value = "/text-asynchronously", produces = MediaType.TEXT_PLAIN_VALUE)
    public Callable<String> getTextAsynchronously() {
        return this::getText;
    }

    /**
     * Gets the empty text.
     *
     * @return the empty text.
     */
    @GetMapping(value = "/empty-text", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getEmptyText() {
        return ResponseEntity.ok()
                .body("");
    }

    /**
     * Gets the JSON.
     *
     * @return the map to be JSON.
     */
    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<?, ?> getJson() {
        return singletonMap("TEST-KEY", "TEST-VALUE");
    }

}
