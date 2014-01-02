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

package org.mongodb.codecs.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassModel<T> {
    private final Class<T> theClass;

    //Two collections to track the fields for performance reasons
    private final List<FieldModel> validatedFields = new ArrayList<FieldModel>();
    private final Map<String, FieldModel> validatedFieldsByName = new HashMap<String, FieldModel>();

    public ClassModel(final Builder<T> builder) {
        this.theClass = builder.getModelClass();

        for (final FieldModel.Builder fieldModelBuilder : builder.getFields()) {
            FieldModel fieldModel = fieldModelBuilder.build();
            validatedFields.add(fieldModel);
            validatedFieldsByName.put(fieldModel.getName(), fieldModel);
        }
    }

    public Object createInstanceOfClass() throws IllegalAccessException, InstantiationException {
        return theClass.newInstance();
    }

    public Collection<FieldModel> getFields() {
        //returning validatedFieldsByName.values is half as fast as simply returning this list
        return validatedFields;
    }

    public FieldModel getDeclaredField(final String fieldName) throws NoSuchFieldException {
        FieldModel fieldModel = validatedFieldsByName.get(fieldName);
        if (fieldModel == null) {
            throw new NoSuchFieldException(String.format("Field %s not found on class %s", fieldName, theClass));
        }
        return fieldModel;
    }

    public Class<T> getModelClass() {
        return theClass;
    }

    @Override
    public String toString() {
        return "ClassModel{"
               + "theClass=" + theClass
               + ", validatedFields=" + validatedFields
               + '}';
    }

    public static class Builder<T> {
        private final Class<T> theClass;
        private final List<FieldModel.Builder> fields = new ArrayList<FieldModel.Builder>();

        public Builder(final Class<T> theClass) {
            this.theClass = theClass;
        }

        public void addField(final FieldModel.Builder field) {
            fields.add(field);
        }

        public void addField(final int index, final FieldModel.Builder field) {
            fields.add(index, field);
        }

        public ClassModel<T> build() {
            return new ClassModel<T>(this);
        }

        public void clearFields() {
            fields.clear();
        }

        public FieldModel.Builder getField(final String fieldName) {
            for (FieldModel.Builder field : fields) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }

            return null;
        }

        public Collection<FieldModel.Builder> getFields() {
            return fields;
        }

        public Class<T> getModelClass() {
            return theClass;
        }

        public void removeField(final String fieldName) {
            for (FieldModel.Builder field : fields) {
                if (field.getName().equals(fieldName)) {
                    fields.remove(field);
                    return;
                }
            }
        }
    }
}