package org.mongodb.codecs.configuration;

import org.mongodb.Codec;
import org.mongodb.codecs.ClassModel;
import org.mongodb.codecs.ClassModelCodec;
import org.mongodb.codecs.configuration.conventions.ModelConvention;

import java.util.ArrayList;

public class ClassModelCodecSource implements CodecSource {

    private final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
    private final ArrayList<ModelConvention> conventions = new ArrayList<ModelConvention>();
    private boolean includeAllClasses = false;

    @Override
    public <T> Codec<T> getCodec(final Class<T> theClass, final CodecFinder finder) {
        if (!includeAllClasses && !classes.contains(theClass)) {
            // we aren't supposed to map this class.
            return null;
        }

        ClassModelBuilder<T> builder = new ClassModelBuilder<T>(theClass);

        for (ModelConvention convention : conventions) {
            convention.apply(builder, finder);
        }

        ClassModel<T> model = builder.build();

        return new ClassModelCodec<T>(model);
    }

    public ClassModelCodecSource addConvention(final ModelConvention convention) {
        conventions.add(convention);
        return this;
    }

    public <T> ClassModelCodecSource include(final Class<T> theClass) {
        classes.add(theClass);
        return this;
    }

    // TODO: I don't think this should be here, but to preserve existing functionality
    // TODO: in PojoCodec, this exists to allow for wildcard mapping of classes
    public <T> ClassModelCodecSource includeAllClasses() {
        includeAllClasses = true;
        return this;
    }
}