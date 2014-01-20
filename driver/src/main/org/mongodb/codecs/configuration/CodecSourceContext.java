package org.mongodb.codecs.configuration;

import org.mongodb.Codec;

public class CodecSourceContext<T> {

    private final Class<T> codecClass;
    private final CodecFinder finder;
    private final CodecSourceContext<?> parent;

    CodecSourceContext(final Class<T> codecClass, final CodecFinder finder) {
        this.codecClass = codecClass;
        this.finder = finder;
        this.parent = null;
    }

    private CodecSourceContext(final CodecSourceContext<?> parent, final Class<T> codecClass) {
        this.codecClass = codecClass;
        this.finder = parent.finder;
        this.parent = parent;
    }

    public <U> Codec<U> findCodec(final Class<U> theClass) {
        if (hasCycles(theClass)) {
            throw new RuntimeException("Need a better exception.  Cycles exist in the class structure.");
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
        CodecSourceContext<?> current = parent;
        while (current != null) {
            if (current.getCodecClass().equals(theClass)) {
                return true;
            }

            current = current.parent;
        }

        return false;
    }
}