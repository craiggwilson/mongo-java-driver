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

package org.mongodb;

import org.junit.After;
import org.junit.Before;
import org.mongodb.codecs.configuration.ClassModelCodecSource;
import org.mongodb.codecs.configuration.CodecRegistryBuilder;
import org.mongodb.codecs.configuration.PrimitiveCodecSource;
import org.mongodb.codecs.configuration.conventions.CodecLookupConvention;
import org.mongodb.codecs.configuration.conventions.DeclaredFieldFinderConvention;

import static org.mongodb.Fixture.getDefaultDatabase;
import static org.mongodb.Fixture.initialiseCollection;

public class DatabaseTestCase {
    //For ease of use and readability, in this specific case we'll allow protected variables
    //CHECKSTYLE:OFF
    protected MongoDatabase database;
    protected MongoCollection<Document> collection;
    protected CodecRegistry codecRegistry;
    //CHECKSTYLE:ON

    @Before
    public void setUp() {
        database = getDefaultDatabase();
        collection = initialiseCollection(database, getClass().getName());

        CodecRegistryBuilder builder = new CodecRegistryBuilder();
        builder.addSource(new PrimitiveCodecSource());

        ClassModelCodecSource classModelSource = new ClassModelCodecSource();
        classModelSource.addConvention(new DeclaredFieldFinderConvention());
        classModelSource.addConvention(new CodecLookupConvention());
        classModelSource.mapAllClasses();
        builder.addSource(classModelSource);

        codecRegistry = builder.build();
    }

    @After
    public void tearDown() {
        if (collection != null) {
            collection.tools().drop();
        }
    }

    protected String getDatabaseName() {
        return database.getName();
    }

    protected String getCollectionName() {
        return collection.getName();
    }
}
