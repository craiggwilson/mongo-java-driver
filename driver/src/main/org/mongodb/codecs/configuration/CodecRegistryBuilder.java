package org.mongodb.codecs.configuration;

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

    public CodecRegistry build() {
        return new CodecRegistryImpl(sources);
    }
}