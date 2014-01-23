package org.mongodb.codecs;

import org.junit.Test;
import org.mongodb.Codec;
import org.mongodb.codecs.configuration.FieldModelBuilder;
import org.mongodb.codecs.configuration.Level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class FieldModelTest {
    @Test
    public void testDefaults() throws NoSuchFieldException {
        FieldModel model = new FieldModelBuilder(TestClass.class.getDeclaredField("one")).build();

        assertEquals("one", model.getName());
        assertNull(model.getCodec());
        assertEquals(true, model.getPersistDefaultValue());
        assertEquals(null, model.getDefaultValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNameIsTheValueSpecifiedInTheBuilder() throws NoSuchFieldException {
        FieldModelBuilder builder = new FieldModelBuilder(TestClass.class.getDeclaredField("one"));
        builder.alias("somethingElse");

        FieldModel model = builder.build();

        assertEquals("somethingElse", model.getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCodecIsTheValueSpecifiedInTheBuilder() throws NoSuchFieldException {
        FieldModelBuilder builder = new FieldModelBuilder(TestClass.class.getDeclaredField("one"));
        StringCodec stringCodec = new StringCodec();
        builder.codec((Codec) stringCodec);

        FieldModel model = builder.build();

        assertSame(stringCodec, model.getCodec());
    }

    @Test
    public void testDefaultValueIsTheValueSpecifiedInTheBuilder() throws NoSuchFieldException {
        FieldModelBuilder builder = new FieldModelBuilder(TestClass.class.getDeclaredField("one"));
        builder.defaultValue("two");

        FieldModel model = builder.build();

        assertSame("two", model.getDefaultValue());
    }

    @Test
    public void testPersistDefaultValueIsTheValueSpecifiedInTheBuilder() throws NoSuchFieldException {
        FieldModelBuilder builder = new FieldModelBuilder(TestClass.class.getDeclaredField("one"));
        builder.persistDefaultValue(false);

        FieldModel model = builder.build();

        assertSame(false, model.getPersistDefaultValue());
    }

    private class TestClass {
        private String one;

        public String getOne() {
            return one;
        }

        public void setOne(final String one) {
            this.one = one;
        }
    }
}