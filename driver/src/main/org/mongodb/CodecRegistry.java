package org.mongodb;

public interface CodecRegistry {
    <T> Codec<T> get(Class<T> theClass);
}