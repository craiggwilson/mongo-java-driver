package org.mongodb.codecs.configuration;

import org.mongodb.Codec;
import org.mongodb.codecs.FieldModel;

import java.lang.reflect.Field;

public class FieldModelBuilder {
    private ModelBuilderValue<Codec<Object>> codec;
    private Field field;
    private ModelBuilderValue<Object> defaultValue;
    private ModelBuilderValue<Boolean> ignoreIfDefault;
    private ModelBuilderValue<String> name;

    FieldModelBuilder(final Field field) {
        this.field = field;
        codec = new ModelBuilderValue<Codec<Object>>(); // can't have a default codec...
        defaultValue = new ModelBuilderValue<Object>(null, Level.DEFAULT);
        ignoreIfDefault = new ModelBuilderValue<Boolean>(false, Level.DEFAULT);
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

    public FieldModelBuilder codec(final Codec<Object> codec) {
        return codec(codec, Level.USER);
    }

    public FieldModelBuilder codec(final Codec<Object> codec, final int level) {
        this.codec.set(codec, level);
        return this;
    }

    public FieldModelBuilder defaultValue(final Object value) {
        return defaultValue(value, Level.USER);
    }

    public FieldModelBuilder defaultValue(final Object value, final int level) {
        this.defaultValue.set(value, level);
        return this;
    }

    public FieldModelBuilder ignoreIfDefault() {
        return ignoreIfDefault(true);
    }

    public FieldModelBuilder ignoreIfDefault(final boolean value) {
        return ignoreIfDefault(value, Level.USER);
    }

    public FieldModelBuilder ignoreIfDefault(final boolean value, final int level) {
        this.ignoreIfDefault.set(value, Level.USER);
        return this;
    }

    public FieldModelBuilder name(final String name) {
        return this.name(name, Level.USER);
    }

    public FieldModelBuilder name(final String name, final int level) {
        this.name.set(name, level);
        return this;
    }
}