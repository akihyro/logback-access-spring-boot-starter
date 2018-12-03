package net.rakugakibox.spring.boot.logback.access.test.asserts;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * The assertion of {@link ResponseEntity}.
 *
 * @param <S> the type of self.
 * @param <A> the type of actual value.
 * @param <B> the type of response entity body.
 */
public class ResponseEntityAssert<S extends ResponseEntityAssert<S, A, B>, A extends ResponseEntity<B>, B>
        extends AbstractAssert<S, A> {

    /**
     * Constructs an instance.
     *
     * @param actual the actual value.
     */
    protected ResponseEntityAssert(A actual) {
        super(actual, ResponseEntityAssert.class);
    }

    /**
     * Constructs an instance.
     *
     * @param actual the actual value.
     * @param selfClass the self class.
     */
    protected ResponseEntityAssert(A actual, Class<?> selfClass) {
        super(actual, selfClass);
    }

    /**
     * Verifies that the status code is equal to the given one.
     *
     * @param status the status.
     * @return this instance.
     * @see ResponseEntity#getStatusCode()
     */
    public S hasStatusCode(HttpStatus status) {
        HttpStatus actualStatusCode = actual.getStatusCode();
        Assertions.assertThat(actualStatusCode).isEqualTo(status);
        return myself;
    }

    /**
     * Verifies that the content length header is greater than or equal to the given one.
     *
     * @param contentLength the minimal content length.
     * @return this instance.
     * @see ResponseEntity#getHeaders()
     * @see HttpHeaders#getContentLength()
     */
    public S hasContentLengthHeader(long contentLength) {
        long actualContentLength = actual.getHeaders().getContentLength();
        Assertions.assertThat(actualContentLength).isGreaterThanOrEqualTo(contentLength);
        return myself;
    }

    /**
     * Verifies that the headers does not contain the content length.
     *
     * @return this instance.
     * @see ResponseEntity#getHeaders()
     * @see HttpHeaders#getContentLength()
     */
    public S doesNotHaveContentLengthHeader() {
        long actualContentLength = actual.getHeaders().getContentLength();
        Assertions.assertThat(actualContentLength).isEqualTo(-1);
        return myself;
    }

    /**
     * Verifies that the body is equal to the given one.
     *
     * @param body the body.
     * @return this instance.
     * @see ResponseEntity#getBody()
     */
    public S hasBody(B body) {
        B actualBody = actual.getBody();
        Assertions.assertThat(actualBody).isEqualTo(body);
        return myself;
    }

    /**
     * Verifies that the headers contains specified header and its value
     *
     * @param headerName the header name.
     * @param headerValue the header value
     * @return this instance.
     * @see ResponseEntity#getHeaders()
     */
    public S hasHeader(String headerName, String headerValue) {
        HttpHeaders headers = actual.getHeaders();
        Assertions.assertThat(headers.getFirst(headerName)).isNotNull()
                .isEqualTo(headerValue);
        return myself;
    }

    /**
     * Creates an assertion.
     *
     * @param <A> the type of actual value.
     * @param <B> the type of response entity body.
     * @param actual the actual value.
     * @return an assertion.
     */
    public static <A extends ResponseEntity<B>, B> ResponseEntityAssert<?, A, B> assertThat(A actual) {
        return new ResponseEntityAssert<>(actual);
    }

}
