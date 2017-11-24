package com.tezhm.wax.factory;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Container for factories to generate.
 */
public class FactoryContainer
{
    Map<String, TypeMirror> factories = new HashMap<>();
    Map<String, ExecutableElement> providers = new HashMap<>();

    /**
     * Add factory type to container.
     *
     * @param element
     */
    public void registerFactory(VariableElement element)
    {
        TypeMirror type = element.asType();
        String name = type.toString();

        if (!this.providers.containsKey(name))
        {
            this.factories.put(name, type);
        }
    }

    public void registerProvider(ExecutableElement provider) throws Exception
    {
        TypeMirror type = provider.getReturnType();
        String name = type.toString();

        // TODO: check that provider has constructor with no arguments - otherwise can't instantiate

        this.factories.remove(name);
        this.providers.put(name, provider);
    }
}
