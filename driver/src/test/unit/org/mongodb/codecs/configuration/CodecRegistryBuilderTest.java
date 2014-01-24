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

    private CodecRegistry subject;

    @Before
    public void before() {
        CodecRegistryBuilder builder = new CodecRegistryBuilder();
        builder.addSource(new PrimitiveCodecSource());

        ClassModelCodecSource classModelSource = new ClassModelCodecSource();
        classModelSource.addConvention(new DeclaredFieldFinderConvention());
        classModelSource.addConvention(new CodecLookupConvention());

        classModelSource.map(Person.class);
        classModelSource.map(Name.class);
        classModelSource.map(Address.class);
        classModelSource.map(CyclePerson.class);
        builder.addSource(classModelSource);

        subject = builder.build();
    }

    @Test
    public void testEncodePerson() {
        Person person = new Person(new Address(), new Name());

        StringWriter sw = new StringWriter();
        JSONWriter writer = new JSONWriter(sw);
        subject.get(Person.class).encode(writer, person);

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
        Person person = subject.get(Person.class).decode(jr);

        Person expected = new Person(new Address(), new Name());

        assertEquals(expected, person);
    }

    @Test
    public void testClassesWithCycles() {
        CyclePerson parent = new CyclePerson();
        parent.setName("parent");

        CyclePerson child = new CyclePerson();
        child.setName("child");

        parent.setChild(child);

        StringWriter sw = new StringWriter();
        JSONWriter writer = new JSONWriter(sw);
        subject.get(CyclePerson.class).encode(writer, parent);

        String json = sw.toString();

        String expected = "{ \"child\" : { \"child\" : null, \"name\" : \"child\" }, \"name\" : \"parent\" }";

        assertEquals(expected, json);
    }

    private static class CyclePerson {
        private CyclePerson child;
        private String name;

        public CyclePerson getChild() {
            return child;
        }

        public String getName() {
            return name;
        }

        public void setChild(final CyclePerson child) {
            this.child = child;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }
}