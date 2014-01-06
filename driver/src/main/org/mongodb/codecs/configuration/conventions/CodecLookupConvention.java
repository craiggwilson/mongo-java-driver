package org.mongodb.codecs.configuration.conventions;

import org.mongodb.Codec;
import org.mongodb.codecs.configuration.CodecFinder;
import org.mongodb.codecs.configuration.FieldModelBuilder;
import org.mongodb.codecs.configuration.Level;

public class CodecLookupConvention extends VisitingModelConvention {

    @Override
    @SuppressWarnings("unchecked")
    protected void visitField(final FieldModelBuilder builder, final CodecFinder codecFinder) {
        builder.setCodec((Codec<Object>) codecFinder.find(builder.getField().getType()), Level.CONVENTIONS);
    }
}