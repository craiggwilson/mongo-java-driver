package org.mongodb.codecs.configuration;

import org.mongodb.Codec;
import org.mongodb.CodecRegistry;

import java.util.ArrayList;

public class CodecRegistryBuilder {
    private final ArrayList<CodecSource> sources = new ArrayList<CodecSource>();

    public CodecRegistryBuilder addSource(final CodecSource source) {
        // we want the order of sources to be probed in reverse.
        // this allows for later sources to override an earlier source.
        sources.add(0, source);
        return this;
    }

    public CodecRegistryBuilder addRegistry(final CodecRegistry registry) {
        if (registry instanceof CodecSource) {
            return addSource((CodecSource) registry);
        }

        return addSource(new CodecRegistryCodecSourceAdapter(registry));
    }

    public CodecRegistry build() {
        return new CodecRegistryImpl(sources);
    }

    private static class CodecRegistryCodecSourceAdapter implements CodecSource {
        private final CodecRegistry registry;

        public CodecRegistryCodecSourceAdapter(CodecRegistry registry) {
            this.registry = registry;
        }

        @Override
        public <T> Codec<T> get(final CodecSourceContext<T> context) {
            try {
                return registry.get(context.getCodecClass());
            } catch (CodecConfigurationException e) {
                return null;
            }
        }
    }
}