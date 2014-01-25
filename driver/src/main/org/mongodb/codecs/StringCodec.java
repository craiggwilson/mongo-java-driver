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
import org.mongodb.Codec;

public class StringCodec implements Codec<String> {
    @Override
    public void encode(final BSONWriter bsonWriter, final String value) {
        if (value == null) {
            bsonWriter.writeNull();
        } else {
            bsonWriter.writeString(value);
        }
    }

    @Override
    public String decode(final BSONReader reader) {
        if (reader.getCurrentBSONType() == BSONType.NULL) {
            reader.readNull();
            return null;
        }

        return reader.readString();
    }

    @Override
    public Class<String> getEncoderClass() {
        return String.class;
    }
}
