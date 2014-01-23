/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.codecs;

import org.mongodb.Codec;
import org.mongodb.codecs.configuration.FieldModelBuilder;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class FieldModel {
    private static final Pattern FIELD_NAME_REGEX_PATTERN = Pattern.compile("([a-zA-Z_][\\w$]*)");

    private final Codec<Object> codec;
    private final Object defaultValue;
    private final Field field;
    private final Boolean ignoreIfDefault;
    private final String name;

    // TODO: don't reference FieldModelBuilder here
    public FieldModel(final FieldModelBuilder builder) {
        if (!isValidFieldName(builder.getName().get())) {
            throw new IllegalArgumentException(String.format("%s is not a valid mongodb field name.", builder.getName()));
        }

        codec = builder.getCodec().get();
        defaultValue = builder.getDefaultValue().get();
        field = builder.getField();
        ignoreIfDefault = builder.getIgnoreIfDefault().get();
        name = builder.getName().get();
    }

    public Object getValue(final Object target) throws IllegalAccessException {
        field.setAccessible(true);
        Object value = field.get(target);
        field.setAccessible(false);
        return value;
    }

    public Codec<Object> getCodec() { return codec; }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Field getField() { return field; }

    public Boolean getIgnoreIfDefault() {
        return ignoreIfDefault;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return field.getType();
    }

    public void setValue(final Object target, final Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(target, value);
        field.setAccessible(false);
    }

    public static boolean isValidFieldName(final String fieldName) {
        //We need to document that fields starting with a $ will be ignored
        //and we probably need to be able to either disable this validation or make it pluggable
        return FIELD_NAME_REGEX_PATTERN.matcher(fieldName).matches();
    }
}