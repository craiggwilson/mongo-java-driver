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
        CodecFinder finder = new CodecFinder() {
            @Override
            public <T> Codec<T> find(final CodecSourceContext<T> context) {
                return get(context);
            }
        };

        CodecSourceContext<T> context = new CodecSourceContext<T>(theClass, finder);
        return get(context);
    }

    @SuppressWarnings("unchecked")
    private <T> Codec<T> get(final CodecSourceContext<T> context) {
        if (!codecs.containsKey(context.getCodecClass())) {
            codecs.putIfAbsent(context.getCodecClass(), getCodec(context));
        }

        return (Codec<T>) codecs.get(context.getCodecClass());
    }

    private <T> Codec<T> getCodec(final CodecSourceContext<T> context) {

        for (CodecSource source : sources) {
            Codec<T> result = source.getCodec(context);
            if (result != null) {
                return result;
            }
        }

        throw new CodecConfigurationException(String.format("Can't find a codec for %s.", context.getCodecClass()));
    }
}
