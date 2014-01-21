package org.mongodb.codecs.configuration.conventions;

import org.junit.Before;
import org.junit.Test;
import org.mongodb.codecs.configuration.ClassModelBuilder;
import org.mongodb.codecs.configuration.FieldModelBuilder;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class DeclaredFieldFinderConventionTest {

    private ClassModelBuilder<TestClass> builder;
    private DeclaredFieldFinderConvention subject;

    @Before
    public void before() {
        builder = new ClassModelBuilder<TestClass>(TestClass.class);
        subject = new DeclaredFieldFinderConvention();
    }

    @Test
    public void testFindsAllDeclaredNonStaticNonTransientFields() {
        subject.apply(builder, null);

        Collection<FieldModelBuilder> fields = builder.getMappedFields();

        assertEquals(2, fields.size());
    }

    private static class TestClass {
        private static int staticInt;
        private transient String readonlyString;
        private String editableString;
        private int editableInt;

        public TestClass() {
            staticInt = 12;
            readonlyString = "can't touch this";
            editableString = "tastes yummy";
            editableInt = 20;
        }

        public static int getStaticInt() {
            return staticInt;
        }

        public String getReadonlyString() {
            return readonlyString;
        }

        public String getEditableString() {
            return editableString;
        }

        public int getEditableInt() {
            return editableInt;
        }
    }
}