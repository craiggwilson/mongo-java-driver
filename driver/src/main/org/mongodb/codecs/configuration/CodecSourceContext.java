package org.mongodb.codecs.configuration;

import org.mongodb.Codec;
import org.mongodb.CodecRegistry;
import org.mongodb.codecs.LazyCodec;
import org.mongodb.util.Factory;
import org.mongodb.util.Lazy;

public class CodecSourceContext<T> {

    private final Class<T> codecClass;
    private final CodecFinder finder;
    private final CodecSourceContext<?> parent;
    private final CodecRegistry registry;

    CodecSourceContext(final CodecRegistry registry, final Class<T> codecClass, final CodecFinder finder) {
        this.codecClass = codecClass;
        this.finder = finder;
        this.parent = null;
        this.registry = registry;
    }

    private CodecSourceContext(final CodecSourceContext<?> parent, final Class<T> codecClass) {
        this.codecClass = codecClass;
        this.finder = parent.finder;
        this.parent = parent;
        this.registry = parent.registry;
    }

    public <U> Codec<U> findCodec(final Class<U> theClass) {
        if (hasCycles(theClass)) {
            Factory<Codec<U>> factory = new Factory<Codec<U>>() {
                @Override
                public Codec<U> get() {
                    return registry.get(theClass);
                }
            };

            return new LazyCodec<U>(new Lazy<Codec<U>>(factory, false));
        }

        CodecSourceContext<U> context = new CodecSourceContext<U>(this, theClass);
        return this.finder.find(context);
    }

    public Class<T> getCodecClass() {
        return codecClass;
    }

    public CodecSourceContext<?> getParent() {
        return parent;
    }

    private <U> Boolean hasCycles(final Class<U> theClass) {
        CodecSourceContext<?> current = this;
        while (current != null) {
            if (current.getCodecClass().equals(theClass)) {
                return true;
            }

            current = current.parent;
        }

        return false;
    }
}