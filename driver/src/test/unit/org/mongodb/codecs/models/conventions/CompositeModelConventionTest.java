package org.mongodb.codecs.models.conventions;

import org.junit.Before;
import org.junit.Test;
import org.mongodb.codecs.models.ClassModel;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompositeModelConventionTest {

    private List<ModelConvention> conventions;
    private CompositeModelConvention subject;

    @Before
    public void before() {
        conventions = Arrays.asList(
                                   (ModelConvention) new TestModelConvention(),
                                   new TestModelConvention(),
                                   new TestModelConvention()
                                   );
        subject = new CompositeModelConvention(conventions);
    }

    @Test
    public void testRunsAllConventionsInOrder() {
        ClassModel.Builder model = new ClassModel.Builder(TestModelConvention.class);

        subject.apply(model);

        for (int i = 0; i < conventions.size(); i++) {
            assertTrue(((TestModelConvention) conventions.get(i)).getWasRun());
            assertEquals(i, ((TestModelConvention) conventions.get(i)).getRunIndex());
        }
    }

    private static class TestModelConvention implements ModelConvention {

        private static int counter;
        private Boolean wasRun;
        private int runIndex;

        @Override
        public void apply(final ClassModel.Builder builder) {
            wasRun = true;
            runIndex = counter++;
        }

        public int getRunIndex() {
            return runIndex;
        }

        public Boolean getWasRun() {
            return wasRun;
        }
    }
}