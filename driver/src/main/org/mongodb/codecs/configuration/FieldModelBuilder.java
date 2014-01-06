package org.mongodb.codecs.configuration;

import org.mongodb.Codec;
import org.mongodb.codecs.FieldModel;

import java.lang.reflect.Field;

public class FieldModelBuilder {
    private ModelBuilderValue<Codec<Object>> codec;
    private Field field;
    private ModelBuilderValue<String> name;

    public FieldModelBuilder(final Field field) {
        this.field = field;
        codec = new ModelBuilderValue<Codec<Object>>(); // can't have a default codec...
        name = new ModelBuilderValue<String>(field.getName(), Level.DEFAULT);
    }

    public FieldModel build() {
        return new FieldModel(this);
    }

    public ModelBuilderValue<Codec<Object>> getCodec() {
        return codec;
    }

    public Field getField() {
        return field;
    }

    public ModelBuilderValue<String> getName() {
        return name;
    }

    public void setCodec(final Codec<Object> codec, final int level) {
        this.codec.set(codec, level);
    }

    public void setName(final String name, final int level) {
        this.name.set(name, level);
    }
}
