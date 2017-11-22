package com.tezhm.wax.internal;

import java.util.HashSet;

import javax.lang.model.type.TypeMirror;

/**
 * Container for factory types.
 */
public class FactorySet extends HashSet<TypeMirror>
{
    /**
     * Add factory type to container.
     *
     * @param factory
     */
    public void register(TypeMirror factory)
    {
        add(factory);
    }
}
