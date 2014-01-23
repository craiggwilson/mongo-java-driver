package org.mongodb.codecs.configuration;

import org.junit.Test;
import org.mongodb.codecs.pojo.Person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClassModelBuilderTest {

    @Test
    public void canMapViaInheritance() {
        PersonClassModelBuilder builder = new PersonClassModelBuilder();

        assertTrue(builder.getSkipConventions());
        assertEquals(2, builder.getMappedFields().size());
    }

    @Test(expected = CodecConfigurationException.class)
    public void throwsExceptionWhenFieldDoesNotExist() {
        ClassModelBuilder<Person> builder = new ClassModelBuilder<Person>(Person.class);

        builder.map("doesNotExist");
    }

    private static class PersonClassModelBuilder extends ClassModelBuilder<Person> {

        public PersonClassModelBuilder() {
            super(Person.class);

            setSkipConventions(true);
            map("address").alias("a").defaultValue(null).persistDefaultValue(false);
            map("name").alias("n");
        }
    }
}