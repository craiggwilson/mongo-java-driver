package org.mongodb.codecs.configuration;

import org.bson.types.CodeWithScope;
import org.mongodb.Codec;
import org.mongodb.Document;
import org.mongodb.codecs.BooleanCodec;
import org.mongodb.codecs.ByteArrayCodec;
import org.mongodb.codecs.ByteCodec;
import org.mongodb.codecs.CodeCodec;
import org.mongodb.codecs.CodeWithScopeCodec;
import org.mongodb.codecs.DateCodec;
import org.mongodb.codecs.DocumentCodec;
import org.mongodb.codecs.DoubleCodec;
import org.mongodb.codecs.FloatCodec;
import org.mongodb.codecs.IntegerCodec;
import org.mongodb.codecs.LongCodec;
import org.mongodb.codecs.MaxKeyCodec;
import org.mongodb.codecs.MinKeyCodec;
import org.mongodb.codecs.NullCodec;
import org.mongodb.codecs.ObjectIdCodec;
import org.mongodb.codecs.PatternCodec;
import org.mongodb.codecs.ShortCodec;
import org.mongodb.codecs.StringCodec;
import org.mongodb.codecs.TimestampCodec;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveCodecSource implements CodecSource {

    private final Map<Class<?>, Codec<?>> codecs = new HashMap<Class<?>, Codec<?>>();

    public PrimitiveCodecSource() {
        addCodec(new ObjectIdCodec());
        addCodec(new IntegerCodec());
        addCodec(new LongCodec());
        addCodec(new StringCodec());
        addCodec(new DoubleCodec());
        addCodec(new DateCodec());
        addCodec(new TimestampCodec());
        addCodec(new BooleanCodec());
        addCodec(new PatternCodec());
        addCodec(new MinKeyCodec());
        addCodec(new MaxKeyCodec());
        addCodec(new CodeCodec());
        addCodec(new NullCodec());
        addCodec(new FloatCodec());
        addCodec(new ShortCodec());
        addCodec(new ByteCodec());
        addCodec(new ByteArrayCodec());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(final CodecSourceContext<T> context) {
        if (codecs.containsKey(context.getCodecClass())) {
            return (Codec<T>) codecs.get(context.getCodecClass());
        }

        if (context.getCodecClass().equals(CodeWithScope.class)) {
            return (Codec<T>) new CodeWithScopeCodec(context.findCodec(Document.class));
        }

        if (context.getCodecClass().equals(Document.class)) {
            return (Codec<T>) new DocumentCodec(context.getRegistry());
        }

        return null;
    }

    private <T> void addCodec(final Codec<T> codec) {
        codecs.put(codec.getEncoderClass(), codec);
    }

}
