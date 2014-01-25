package org.mongodb.codecs.configuration;

import org.mongodb.Codec;
import org.mongodb.CodecRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class CodecRegistryImpl implements CodecRegistry, CodecSource {

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

        CodecSourceContext<T> context = new CodecSourceContext<T>(this, theClass, finder);
        Codec<T> result = get(context);

        if (result == null) {
            throw new CodecConfigurationException(String.format("Can't find a codec for %s.", context.getCodecClass()));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(final CodecSourceContext<T> context) {
        if (!codecs.containsKey(context.getCodecClass())) {
            codecs.putIfAbsent(context.getCodecClass(), getCodecFromSources(context));
        }

        return (Codec<T>) codecs.get(context.getCodecClass());
    }

    private <T> Codec<T> getCodecFromSources(final CodecSourceContext<T> context) {
        for (CodecSource source : sources) {
            Codec<T> result = source.get(context);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }
}
