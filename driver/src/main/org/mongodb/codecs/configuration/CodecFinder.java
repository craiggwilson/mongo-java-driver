package org.mongodb.codecs.configuration;

import org.mongodb.Codec;

interface CodecFinder {

    <T> Codec<T> find(CodecSourceContext<T> context);

}
