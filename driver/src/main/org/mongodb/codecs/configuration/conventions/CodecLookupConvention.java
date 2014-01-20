package org.mongodb.codecs.configuration.conventions;

import org.mongodb.Codec;
import org.mongodb.codecs.configuration.ClassModelBuilder;
import org.mongodb.codecs.configuration.CodecSourceContext;
import org.mongodb.codecs.configuration.FieldModelBuilder;
import org.mongodb.codecs.configuration.Level;

public class CodecLookupConvention extends VisitingModelConvention {

    @Override
    @SuppressWarnings("unchecked")
    protected void visitField(final FieldModelBuilder builder, final CodecSourceContext<?> context) {
        builder.setCodec((Codec<Object>) context.findCodec(builder.getField().getType()), Level.CONVENTIONS);
    }
}