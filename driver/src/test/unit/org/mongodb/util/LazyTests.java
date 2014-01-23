package org.mongodb.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LazyTests {

    private IntegerFactory factory;
    private Lazy<Integer> subject;

    @Test
    public void testCorrectnessWhenSynchronized() {
        IntegerFactory factory = new IntegerFactory();
        Lazy<Integer> lazy = new Lazy<Integer>(factory);

        Integer value1 = lazy.getValue();
        Integer value2 = lazy.getValue();

        assertEquals(value1, value2);
        assertEquals(1, factory.getInvocationCount());
    }

    @Test
    public void testCorrectnessWhenNotSynchronized() {
        IntegerFactory factory = new IntegerFactory();
        Lazy<Integer> lazy = new Lazy<Integer>(factory, false);

        Integer value1 = lazy.getValue();
        Integer value2 = lazy.getValue();

        assertEquals(value1, value2);
        assertEquals(1, factory.getInvocationCount());
    }

    private static class IntegerFactory implements Factory<Integer> {

        private int start = 42;
        private int invocationCount = 0;

        public int getInvocationCount() {
            return invocationCount;
        }

        @Override
        public Integer get() {
            invocationCount++;
            return start++;
        }
    }
}