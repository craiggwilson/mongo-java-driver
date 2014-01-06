package org.mongodb.codecs.configuration;

import org.mongodb.Codec;

public interface CodecFinder {

    <T> Codec<T> find(Class<T> theClass);

}
