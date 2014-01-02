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

import org.bson.BSONWriter;
import org.mongodb.Encoder;
import org.mongodb.codecs.models.ClassModel;
import org.mongodb.codecs.models.FieldModel;

import java.util.HashMap;
import java.util.Map;

public class PojoEncoder<T> implements Encoder<T> {
    private final Codecs codecs;

    //at this time, this seems to be the only way to
    @SuppressWarnings("rawtypes")
    private final Map<Class<?>, ClassModel> classModelForClass = new HashMap<Class<?>, ClassModel>();

    public PojoEncoder(final Codecs codecs) {
        this.codecs = codecs;
    }

    @Override
    public void encode(final BSONWriter bsonWriter, final T value) {
        bsonWriter.writeStartDocument();
        encodePojo(bsonWriter, value);
        bsonWriter.writeEndDocument();
    }

    @SuppressWarnings({"unchecked", "rawtypes"}) //bah.  maybe this isn't even correct
    private void encodePojo(final BSONWriter bsonWriter, final T value) {
        ClassModel<?> classModel = classModelForClass.get(value.getClass());
        if (classModel == null) {
            classModel = PojoCodec.buildClassModel(value.getClass());
            classModelForClass.put(value.getClass(), classModel);
        }
        for (final FieldModel field : classModel.getFields()) {
            encodeField(bsonWriter, value, field);
        }
    }

    // need to cast the field
    @SuppressWarnings("unchecked")
    private void encodeField(final BSONWriter bsonWriter, final T value, final FieldModel field) {
        try {
            T fieldValue = (T) field.getValue(value);
            bsonWriter.writeName(field.getName());
            encodeValue(bsonWriter, fieldValue);
        } catch (IllegalAccessException e) {
            //TODO: this is really going to bugger up the writer if it throws an exception halfway through writing
            throw new EncodingException("Could not encode field '" + field.getName() + "' from " + value, e);
        }
    }

    private void encodeValue(final BSONWriter bsonWriter, final T fieldValue) {
        if (codecs.canEncode(fieldValue)) {
            codecs.encode(bsonWriter, fieldValue);
        } else {
            encode(bsonWriter, fieldValue);
        }
    }

    @Override
    public Class<T> getEncoderClass() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
