package org.mongodb.codecs.configuration;

import org.junit.Test;
import org.mongodb.Codec;
import org.mongodb.codecs.FieldModel;
import org.mongodb.codecs.StringCodec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class FieldModelTest {
    @Test
    public void testDefaults() throws NoSuchFieldException {
        FieldModel model = new FieldModelBuilder(TestClass.class.getDeclaredField("one")).build();

        assertEquals("one", model.getName());
        assertNull(model.getCodec());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNameIsTheValueSpecifiedInTheBuilder() throws NoSuchFieldException {
        FieldModelBuilder builder = new FieldModelBuilder(TestClass.class.getDeclaredField("one"));
        builder.name("somethingElse", Level.USER);

        FieldModel model = builder.build();

        assertEquals("somethingElse", model.getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCodecIsTheValueSpecifiedInTheBuilder() throws NoSuchFieldException {
        FieldModelBuilder builder = new FieldModelBuilder(TestClass.class.getDeclaredField("one"));
        StringCodec stringCodec = new StringCodec();
        builder.codec((Codec) stringCodec, Level.USER);

        FieldModel model = builder.build();

        assertSame(stringCodec, model.getCodec());
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