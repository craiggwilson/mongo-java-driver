package org.mongodb.codecs;

import org.bson.BSONReader;
import org.bson.BSONWriter;
import org.mongodb.Codec;
import org.mongodb.util.Lazy;

public class LazyCodec<T> implements Codec<T> {

    private final Lazy<Codec<T>> lazyCodec;

    public LazyCodec(final Lazy<Codec<T>> lazyCodec) {
        this.lazyCodec = lazyCodec;
    }

    @Override
    public <E> T decode(final BSONReader reader) {
        return lazyCodec.getValue().decode(reader);
    }

    @Override
    public void encode(final BSONWriter bsonWriter, final T value) {
        lazyCodec.getValue().encode(bsonWriter, value);
    }

    @Override
    public Class<T> getEncoderClass() {
        return lazyCodec.getValue().getEncoderClass();
    }
}