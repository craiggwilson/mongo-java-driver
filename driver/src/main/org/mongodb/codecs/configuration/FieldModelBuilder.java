package org.mongodb.codecs.configuration;

import org.mongodb.Codec;
import org.mongodb.codecs.FieldModel;

import java.lang.reflect.Field;

public class FieldModelBuilder {
    private ModelBuilderValue<String> alias;
    private ModelBuilderValue<Codec<Object>> codec;
    private Field field;
    private ModelBuilderValue<Object> defaultValue;
    private ModelBuilderValue<Boolean> persistDefaultValue;

    public FieldModelBuilder(final Field field) {
        this.field = field;
        alias = new ModelBuilderValue<String>(field.getName(), Level.DEFAULT);
        codec = new ModelBuilderValue<Codec<Object>>(); // can't have a default codec...
        defaultValue = new ModelBuilderValue<Object>(null, Level.DEFAULT);
        persistDefaultValue = new ModelBuilderValue<Boolean>(true, Level.DEFAULT);
    }

    public FieldModel build() {
        return new FieldModel(this);
    }

    public ModelBuilderValue<String> getAlias() {
        return alias;
    }

    public ModelBuilderValue<Codec<Object>> getCodec() {
        return codec;
    }

    public ModelBuilderValue<Object> getDefaultValue() {
        return defaultValue;
    }

    public Field getField() {
        return field;
    }

    public ModelBuilderValue<Boolean> getPersistDefaultValue() {
        return persistDefaultValue;
    }

    public FieldModelBuilder alias(final String name) {
        return alias(name, Level.USER);
    }

    public FieldModelBuilder alias(final String name, final int level) {
        alias.set(name, level);
        return this;
    }

    @SuppressWarnings("unchecked")
    public FieldModelBuilder codec(final Codec codec) {
        return codec(codec, Level.USER);
    }

    @SuppressWarnings("unchecked")
    public FieldModelBuilder codec(final Codec codec, final int level) {
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

    public FieldModelBuilder persistDefaultValue(final boolean value) {
        return persistDefaultValue(value, Level.USER);
    }

    public FieldModelBuilder persistDefaultValue(final boolean value, final int level) {
        this.persistDefaultValue.set(value, Level.USER);
        return this;
    }
}