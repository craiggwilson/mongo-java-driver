package org.mongodb.codecs.configuration;

import org.junit.Before;
import org.junit.Test;
import org.mongodb.Codec;
import org.mongodb.CodecRegistry;
import org.mongodb.codecs.configuration.conventions.CodecLookupConvention;
import org.mongodb.codecs.configuration.conventions.DeclaredFieldFinderConvention;
import org.mongodb.codecs.pojo.Address;
import org.mongodb.codecs.pojo.Name;
import org.mongodb.codecs.pojo.Person;
import org.mongodb.json.JSONReader;
import org.mongodb.json.JSONWriter;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;


public class CodecRegistryBuilderTest {

    private Codec<Person> personCodec;

    @Before
    public void before() {
        CodecRegistryBuilder builder = new CodecRegistryBuilder();
        builder.addSource(new PrimitiveCodecSource());

        ClassModelCodecSource classModelSource = new ClassModelCodecSource();
        classModelSource.addConvention(new DeclaredFieldFinderConvention());
        classModelSource.addConvention(new CodecLookupConvention());
        classModelSource.include(Person.class);
        classModelSource.include(Name.class);
        classModelSource.include(Address.class);
        builder.addSource(classModelSource);

        CodecRegistry registry = builder.build();

        personCodec = registry.get(Person.class);
    }

    @Test
    public void testEncodePerson() {
        Person person = new Person(new Address(), new Name());

        StringWriter sw = new StringWriter();
        JSONWriter writer = new JSONWriter(sw);
        personCodec.encode(writer, person);

        String json = sw.toString();

        String expected = "{ \"address\" : { \"address1\" : \"Flat 1\", \"address2\" : \"Town\" }, \"name\" : { \"firstName\" : \"Eric\","
                          + " \"surname\" : \"Smith\" } }";

        assertEquals(expected, json);
    }

    @Test
    public void testDecodePerson() {
        String json = "{ \"address\" : { \"address1\" : \"Flat 1\", \"address2\" : \"Town\" }, \"name\" : { \"firstName\" : \"Eric\","
                      + " \"surname\" : \"Smith\" } }";

        JSONReader jr = new JSONReader(json);
        Person person = personCodec.decode(jr);

        Person expected = new Person(new Address(), new Name());

        assertEquals(expected, person);
    }
}