package com.tezhm.wax.internal;

import org.junit.Test;
import org.mockito.Mockito;

import javax.lang.model.type.TypeMirror;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClassFieldMapTest
{
    @Test
    public void registerClass() throws Exception
    {
        ClassFieldMap testClass = new ClassFieldMap();
        TypeMirror cls = Mockito.mock(TypeMirror.class);
        testClass.registerClass(cls);
        assertEquals(1, testClass.size());
        assertTrue(testClass.keySet().contains(cls));
    }

    @Test
    public void registerClassDuplicate() throws Exception
    {
        ClassFieldMap testClass = new ClassFieldMap();
        TypeMirror cls = Mockito.mock(TypeMirror.class);
        testClass.registerClass(cls);
        testClass.registerClass(cls);
        assertEquals(1, testClass.size());
        assertTrue(testClass.keySet().contains(cls));
    }

    @Test
    public void registerMultipleClasses() throws Exception
    {

    }

    @Test
    public void registerField() throws Exception
    {

    }

    @Test
    public void registerFieldNoClass() throws Exception
    {

    }

    @Test
    public void registerFieldDuplicate() throws Exception
    {

    }

    @Test
    public void registerFieldMultipleClasses() throws Exception
    {

    }

    @Test
    public void registerMultipleFields() throws Exception
    {

    }
}
