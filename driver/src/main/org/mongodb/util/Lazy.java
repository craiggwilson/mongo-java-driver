package org.mongodb.util;

public class Lazy<T> {
    private final Factory<T> factory;
    private final boolean requireSynchronization;

    private boolean hasValue;
    private T value;

    public Lazy(final Factory<T> factory) {
        this(factory, true);
    }

    public Lazy(final Factory<T> factory, final boolean requireSynchronization) {
        this.factory = factory;
        this.requireSynchronization = requireSynchronization;
    }

    public T getValue() {
        if (requireSynchronization) {
            return getValueSynchronized();
        }

        if (!hasValue) {
            value = factory.get();
            hasValue = true;
        }

        return value;
    }

    private synchronized T getValueSynchronized() {
        if (!hasValue) {
            value = factory.get();
            hasValue = true;
        }

        return value;
    }
}