package org.mongodb.codecs.configuration;

import org.mongodb.Codec;
import org.mongodb.CodecRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class CodecRegistryImpl implements CodecRegistry {

    private final ConcurrentMap<Class<?>, Codec<?>> codecs = new ConcurrentHashMap<Class<?>, Codec<?>>();
    private final ArrayList<CodecSource> sources;

    public CodecRegistryImpl(final Collection<CodecSource> sources) {
        this.sources = new ArrayList<CodecSource>(sources);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(final Class<T> theClass) {
        if (!codecs.containsKey(theClass)) {
            codecs.putIfAbsent(theClass, getCodec(theClass));
        }

        return (Codec<T>) codecs.get(theClass);
    }

    private <T> Codec<T> getCodec(final Class<T> theClass) {

        CodecFinder finder = new CodecFinder() {
            @Override
            public <T> Codec<T> find(final Class<T> theClass) {

                // CodecFinder should have a context rather than just
                // a class.  Having a context would allow us to track
                // and satisfy cyclic codec references.
                return get(theClass);
            }
        };

        for (CodecSource source : sources) {
            Codec<T> result = source.getCodec(theClass, finder);
            if (result != null) {
                return result;
            }
        }

        throw new IllegalArgumentException(String.format("Can't find a codec for %s.", theClass));
    }
}
