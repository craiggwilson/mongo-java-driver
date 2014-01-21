package org.mongodb.codecs.configuration;

import org.mongodb.codecs.ClassModel;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;

public class ClassModelBuilder<T> {
    private final Class<T> theClass;
    private final LinkedHashMap<String, FieldModelBuilder> fields;
    private boolean skipConventions;

    public ClassModelBuilder(final Class<T> theClass) {
        this.theClass = theClass;
        fields = new LinkedHashMap<String, FieldModelBuilder>();
    }

    public ClassModel<T> build() {
        return new ClassModel<T>(this);
    }

    public Collection<FieldModelBuilder> getMappedFields() {
        return fields.values();
    }

    public Class<T> getModelClass() {
        return theClass;
    }

    public Boolean getSkipConventions() {
        return skipConventions;
    }

    public FieldModelBuilder map(final String fieldName) {
        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }

        try {
            Field field = theClass.getDeclaredField(fieldName);
            return map(field);
        }
        catch (NoSuchFieldException e) {
            throw new CodecConfigurationException(String.format("A field with the name %s does not exist.", fieldName), e);
        }
    }

    public FieldModelBuilder map(final Field field) {
        FieldModelBuilder builder;
        if (fields.containsKey(field.getName())) {
            builder = fields.get(field.getName());
            if (!builder.getField().equals(field)) {
                throw new RuntimeException("Better exception");
            }
        }

        builder = new FieldModelBuilder(field);
        fields.put(field.getName(), builder);
        return builder;
    }

    public void removeField(final String fieldName) {
        fields.remove(fieldName);
    }

    public ClassModelBuilder<T> setSkipConventions(final boolean skipConventions) {
        this.skipConventions = skipConventions;
        return this;
    }
}