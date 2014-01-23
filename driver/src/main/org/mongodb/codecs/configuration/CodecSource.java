package org.mongodb.codecs.configuration;

import org.mongodb.Codec;


public interface CodecSource {

    <T> Codec<T> getCodec(CodecSourceContext<T> context);

}