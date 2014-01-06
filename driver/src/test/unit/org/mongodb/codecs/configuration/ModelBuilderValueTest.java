package org.mongodb.codecs.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModelBuilderValueTest {

    @Test
    public void testGet() {
        ModelBuilderValue<String> v = new ModelBuilderValue<String>("fun", 2);

        assertEquals("fun", v.get());
    }

    @Test
    public void testGetLevel() {
        ModelBuilderValue<String> v = new ModelBuilderValue<String>("fun", 2);

        assertEquals(2, v.getLevel());
    }

    @Test
    public void testSetShouldOverrideValueWhenLevelIsGreater() {
        ModelBuilderValue<String> v = new ModelBuilderValue<String>("fun", 2);

        v.set("awesome", 3);
        assertEquals("awesome", v.get());
        assertEquals(3, v.getLevel());
    }

    @Test
    public void testSetShouldNotOverrideValueIfLevelIsEqual() {
        ModelBuilderValue<String> v = new ModelBuilderValue<String>("fun", 2);

        v.set("awesome", 2);
        assertEquals("fun", v.get());
        assertEquals(2, v.getLevel());
    }

    @Test
    public void testSetShouldNotOverrideValueIfLevelIsLess() {
        ModelBuilderValue<String> v = new ModelBuilderValue<String>("fun", 2);

        v.set("awesome", 1);
        assertEquals("fun", v.get());
        assertEquals(2, v.getLevel());
    }
}