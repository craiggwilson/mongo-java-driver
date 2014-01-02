package org.mongodb.codecs.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldModelTest {
    @Test
    public void testDefaultNameIsTheFieldName() throws NoSuchFieldException {
        FieldModel model = new FieldModel.Builder(TestClass.class.getDeclaredField("one")).build();

        assertEquals("one", model.getName());
    }

    @Test
    public void testNameIsTheValueSpecifiedInTheBuilder() throws NoSuchFieldException {
        FieldModel.Builder builder = new FieldModel.Builder(TestClass.class.getDeclaredField("one"));
        builder.setName("somethingElse", Level.DEFAULT);

        FieldModel model = builder.build();

        assertEquals("somethingElse", model.getName());
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