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
import org.bson.BSONWriter;
import org.bson.types.BSONTimestamp;
import org.bson.types.Code;
import org.bson.types.CodeWithScope;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;
import org.mongodb.Codec;
import org.mongodb.CodecRegistry;
import org.mongodb.Decoder;
import org.mongodb.Document;
import org.mongodb.codecs.configuration.CodecRegistryBuilder;
import org.mongodb.codecs.validators.QueryFieldNameValidator;
import org.mongodb.codecs.validators.Validator;

import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import static java.lang.String.format;

// TODO: decode into DBRef?
public class DocumentCodec implements Codec<Document> {
    private final Validator<String> fieldNameValidator;
    private final CodecRegistry codecRegistry;

    public DocumentCodec() {
        this(CodecRegistryBuilder.getDefault());
    }

    public DocumentCodec(final CodecRegistry codecRegistry) {
        this(codecRegistry, new QueryFieldNameValidator());
    }

    protected DocumentCodec(final CodecRegistry codecRegistry, final Validator<String> fieldNameValidator) {
        if (codecRegistry == null) {
            throw new IllegalArgumentException("codecRegistry is null");
        }
        this.codecRegistry = codecRegistry;
        this.fieldNameValidator = fieldNameValidator;
    }

    @Override
    public void encode(final BSONWriter bsonWriter, final Document document) {
        bsonWriter.writeStartDocument();

        beforeFields(bsonWriter, document);

        for (final Map.Entry<String, Object> entry : document.entrySet()) {
            fieldNameValidator.validate(entry.getKey());

            if (skipField(entry.getKey())) {
                continue;
            }
            bsonWriter.writeName(entry.getKey());
            writeValue(bsonWriter, entry.getValue());
        }
        bsonWriter.writeEndDocument();
    }

    protected void beforeFields(final BSONWriter bsonWriter, final Document document) {
    }

    protected boolean skipField(final String key) {
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void writeValue(final BSONWriter bsonWriter, final Object value) {
        if (value == null) {
            bsonWriter.writeNull();
        }

        Codec codec = codecRegistry.get(value.getClass());
        codec.encode(bsonWriter, value);
    }

    @Override
    public Document decode(final BSONReader reader) {
        Document document = new Document();

        reader.readStartDocument();
        while (reader.readBSONType() != BSONType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            document.put(fieldName, readValue(reader, fieldName));
        }

        reader.readEndDocument();

        return document;
    }
    
    protected Object readValue(final BSONReader reader, final String fieldName) {
        BSONType bsonType = reader.getCurrentBSONType();
        if (bsonType.equals(BSONType.DOCUMENT)) {
            return this.decode(reader);
        } else {
            return getCodecFromBSONType(bsonType).decode(reader);
        }
    }

    protected Decoder getCodecFromBSONType(final BSONType bsonType) {
        switch (bsonType) {
            case DOUBLE:
                return codecRegistry.get(Double.class);
            case STRING:
                return codecRegistry.get(String.class);
            case DOCUMENT:
                return this;
            case OBJECT_ID:
                return codecRegistry.get(ObjectId.class);
            case BOOLEAN:
                return codecRegistry.get(Boolean.class);
            case DATE_TIME:
                return codecRegistry.get(Date.class);
            case REGULAR_EXPRESSION:
                return codecRegistry.get(Pattern.class);
            case JAVASCRIPT:
                return codecRegistry.get(Code.class);
            case JAVASCRIPT_WITH_SCOPE:
                return codecRegistry.get(CodeWithScope.class);
            case INT32:
                return codecRegistry.get(Integer.class);
            case TIMESTAMP:
                return codecRegistry.get(BSONTimestamp.class);
            case INT64:
                return codecRegistry.get(Long.class);
            case MIN_KEY:
                return codecRegistry.get(MinKey.class);
            case MAX_KEY:
                return codecRegistry.get(MaxKey.class);
            default:
                throw new UnsupportedOperationException(format("Cannot decode BSONType %s", bsonType));
        }
    }

    @Override
    public Class<Document> getEncoderClass() {
        return Document.class;
    }

}
