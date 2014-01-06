package org.mongodb.codecs.configuration;

public class ModelBuilderValue<T> {

    private int level;
    private T value;

    public ModelBuilderValue() {
        level = Level.UNSET;
        value = null;
    }

    public ModelBuilderValue(final T value, final int level) {
        this.value = value;
        this.level = level;
    }

    public T get() {
        return value;
    }

    public int getLevel() {
        return level;
    }

    public void set(final T value, final int level) {
        if (level > this.level) {
            this.level = level;
            this.value = value;
        }
    }
}