package net.rakugakibox.spring.boot.logback.access;

import java.io.Serializable;
import java.util.Optional;

/**
 * The base class that indicates the overridden attribute.
 * The value is optional and it is lazily evaluated.
 * If the evaluated value is present, caches it. Otherwise, returns the original value.
 *
 * @param <T> the type of value.
 */
public abstract class AbstractOverridenAttribute<T extends Serializable> implements Serializable {

    /**
     * Whether was evaluated.
     */
    private boolean evaluated;

    /**
     * The evaluated value.
     */
    private T value;

    /**
     * Returns the value.
     *
     * @return the value.
     */
    public T get() {
        if (!evaluated) {
            value = evaluateValueToOverride().orElse(null);
            evaluated = true;
        }
        return Optional.ofNullable(value).orElseGet(this::getOriginalValue);
    }

    /**
     * Evaluates the value to override.
     *
     * @return the value to override.
     */
    protected abstract Optional<T> evaluateValueToOverride();

    /**
     * Returns the original value.
     *
     * @return the original value.
     */
    protected abstract T getOriginalValue();

}

