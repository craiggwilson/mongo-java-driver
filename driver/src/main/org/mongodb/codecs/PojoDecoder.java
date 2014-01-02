/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
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

import org.bson.BSONReader;
import org.bson.BSONType;
import org.mongodb.Decoder;
import org.mongodb.codecs.models.ClassModel;
import org.mongodb.codecs.models.FieldModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PojoDecoder implements Decoder<Object> {
    private final Codecs codecs;

    //at this time, this seems to be the only way to
    @SuppressWarnings("rawtypes")
    private final Map<Class<?>, ClassModel> fieldsForClass = new HashMap<Class<?>, ClassModel>();

    public PojoDecoder(final Codecs codecs) {
        this.codecs = codecs;
    }

    @Override
    public Object decode(final BSONReader reader) {
        return decode(reader, Object.class);
    }

    public <T> T decode(final BSONReader reader, final Class<T> theClass) {
        T pojo;
        try {
            reader.readStartDocument();
            pojo = decodePojo(reader, theClass);
            reader.readEndDocument();
        } catch (IllegalAccessException e) {
            throw new DecodingException("Could not decode into '" + theClass, e);
        }
        return pojo;
    }

    @SuppressWarnings("unchecked") // bah
    private <T> T decodePojo(final BSONReader reader, final Class<T> theClass) throws IllegalAccessException {
        ClassModel classModel = fieldsForClass.get(theClass);
        if (classModel == null) {
            classModel = PojoCodec.buildClassModel(theClass);
            fieldsForClass.put(theClass, classModel);
        }
        try {
            T pojo = (T) classModel.createInstanceOfClass();
            while (reader.readBSONType() != BSONType.END_OF_DOCUMENT) {
                FieldModel fieldOnPojo = getPojoFieldForNextValue(reader.readName(), classModel);
                Object decodedValue;
                if (reader.getCurrentBSONType() == BSONType.DOCUMENT && !codecs.canDecode(fieldOnPojo.getType())) {
                    decodedValue = decode(reader, fieldOnPojo.getType());
                } else if (reader.getCurrentBSONType() == BSONType.DOCUMENT) {
                    decodedValue = decodeMap(reader, fieldOnPojo);
                } else if (reader.getCurrentBSONType() == BSONType.ARRAY) {
                    decodedValue = decodeIterable(reader, fieldOnPojo);
                } else {
                    decodedValue = codecs.decode(reader);
                }
                fieldOnPojo.getField().setAccessible(true);
                fieldOnPojo.getField().set(pojo, decodedValue);
                fieldOnPojo.getField().setAccessible(false);
            }
            return pojo;
        }
        catch (InstantiationException e) {
            throw new DecodingException(String.format("Can't create an instance of %s", theClass), e);
        }
    }

    private <E> Map<String, E> decodeMap(final BSONReader reader, final FieldModel fieldOnPojo) {
        Type[] actualTypeArguments = ((ParameterizedType) fieldOnPojo.getField().getGenericType()).getActualTypeArguments();
        Type typeOfItemsInMap = actualTypeArguments[1];
        Map<String, E> map = new HashMap<String, E>();

        reader.readStartDocument();
        while (reader.readBSONType() != BSONType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            E value = getValue(reader, (Class) typeOfItemsInMap);
            map.put(fieldName, value);
        }

        reader.readEndDocument();
        return map;
    }

    private <E> Iterable<E> decodeIterable(final BSONReader reader, final FieldModel fieldOnPojo) {
        Class<?> classOfIterable = fieldOnPojo.getType();
        if (classOfIterable.isArray()) {
            throw new DecodingException("Decoding into arrays is not supported.  Either provide a custom decoder, or use a List.");
        }

        Collection<E> collection;
        if (Set.class.isAssignableFrom(classOfIterable)) {
            collection = new HashSet<E>();
        } else {
            collection = new ArrayList<E>();
        }

        Type[] actualTypeArguments = ((ParameterizedType) fieldOnPojo.getField().getGenericType()).getActualTypeArguments();
        Type typeOfItemsInList = actualTypeArguments[0];

        reader.readStartArray();
        //TODO: this is still related to the problem that we can't reuse the IterableCodec, or tell it to call back to here
        while (reader.readBSONType() != BSONType.END_OF_DOCUMENT) {
            E value = getValue(reader, (Class) typeOfItemsInList);
            collection.add(value);
        }

        reader.readEndArray();
        return collection;
    }

    // Need to cast into type E when decoding into a collection
    @SuppressWarnings("unchecked")
    private <E> E getValue(final BSONReader reader, final Class<?> typeOfItemsInList) {
        E value;
        if (codecs.canDecode(typeOfItemsInList)) {
            value = (E) codecs.decode(reader);
        } else {
            value = (E) decode(reader, typeOfItemsInList);
        }
        return value;
    }

    private <T> FieldModel getPojoFieldForNextValue(final String nameOfField, final ClassModel classModel) {
        try {
            return classModel.getDeclaredField(nameOfField);
        } catch (NoSuchFieldException e) {
            throw new DecodingException(String.format("Could not decode field '%s' into '%s'", nameOfField, classModel), e);
        }
    }
}
