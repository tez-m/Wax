package com.tezhm.wax.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * Container for classes and associated fields.
 */
public class ClassFieldMap extends HashMap<TypeMirror, Set<Element>>
{
    /**
     * Add class to container. Safe to call on duplicate class keys.
     *
     * @param cls
     */
    public void registerClass(TypeMirror cls)
    {
        if (!containsKey(cls))
        {
            put(cls, new HashSet<Element>());
        }
    }

    /**
     * Add field to a class container. Safe to call on duplicate class keys and duplicate fields.
     *
     * @param cls
     * @param field
     */
    public void registerField(TypeMirror cls, Element field)
    {
        registerClass(cls);
        get(cls).add(field);
    }
}
