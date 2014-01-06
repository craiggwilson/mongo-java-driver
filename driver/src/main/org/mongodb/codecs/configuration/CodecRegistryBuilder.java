package org.mongodb.codecs.configuration;

import org.mongodb.CodecRegistry;

import java.util.ArrayList;

public class CodecRegistryBuilder {

    private final ArrayList<CodecSource> sources = new ArrayList<CodecSource>();

    public CodecRegistryBuilder addSource(final CodecSource source) {
        sources.add(source);
        return this;
    }

    public CodecRegistry build() {
        return new CodecRegistryImpl(sources);
    }
}