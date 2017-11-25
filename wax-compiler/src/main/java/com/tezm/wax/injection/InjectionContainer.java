package com.tezm.wax.injection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * Container for injections within classes.
 */
public class InjectionContainer
{
    Map<TypeMirror, Set<Element>> fields = new HashMap<>();
    Map<TypeMirror, Set<Element>> variables = new HashMap<>();

    /**
     * Add class to container. Safe to call on duplicate class keys.
     *
     * @param cls
     */
    public void registerClass(TypeMirror cls)
    {
        if (!this.fields.containsKey(cls))
        {
            this.fields.put(cls, new HashSet<>());
        }

        if (!this.fields.containsKey(cls))
        {
            this.fields.put(cls, new HashSet<>());
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
        this.fields.get(cls).add(field);
    }

    /**
     * Add field to a class container. Safe to call on duplicate class keys and duplicate fields.
     *
     * @param cls
     * @param field
     */
    public void registerVariable(TypeMirror cls, Element field)
    {
        registerClass(cls);
        this.variables.get(cls).add(field);
    }
}
