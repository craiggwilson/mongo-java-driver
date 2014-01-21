package org.mongodb.codecs.configuration;

import org.mongodb.Codec;
import org.mongodb.codecs.ClassModel;
import org.mongodb.codecs.ClassModelCodec;
import org.mongodb.codecs.configuration.conventions.ModelConvention;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ClassModelCodecSource implements CodecSource {

    private final LinkedHashMap<Class<?>, ClassModelBuilder<?>> classes = new LinkedHashMap<Class<?>, ClassModelBuilder<?>>();
    private final ArrayList<ModelConvention> conventions = new ArrayList<ModelConvention>();
    private boolean mapAllClasses = false;

    @Override
    public <T> Codec<T> getCodec(final CodecSourceContext<T> context) {

        Class<T> theClass = context.getCodecClass();

        if (!mapAllClasses && !classes.containsKey(theClass)) {
            // we aren't supposed to map this class.
            return null;
        }

        ClassModelBuilder<T> builder;
        if (classes.containsKey(theClass)) {
            builder = (ClassModelBuilder<T>) classes.get(theClass);
        }
        else {
            builder = new ClassModelBuilder<T>(theClass);
        }

        if (!builder.getSkipConventions()) {
            for (ModelConvention convention : conventions) {
                convention.apply(builder, context);
            }
        }

        ClassModel<T> model = builder.build();

        return new ClassModelCodec<T>(model);
    }

    public <T> ClassModelBuilder<T> addBuilder(final ClassModelBuilder<T> builder) {
        classes.put(builder.getModelClass(), builder);
        return builder;
    }

    public ClassModelCodecSource addConvention(final ModelConvention convention) {
        conventions.add(convention);
        return this;
    }

    // TODO: I don't think this should be here, but to preserve existing functionality
    // TODO: in PojoCodec, this exists to allow for wildcard mapping of classes
    public <T> ClassModelCodecSource mapAllClasses() {
        mapAllClasses = true;
        return this;
    }

    public <T> ClassModelBuilder<T> map(final Class<T> theClass) {
        if (classes.containsKey(theClass)) {
            return (ClassModelBuilder<T>) classes.get(theClass);
        }

        ClassModelBuilder<T> builder = new ClassModelBuilder<T>(theClass);
        classes.put(theClass, builder);
        return builder;
    }
}