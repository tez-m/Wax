package com.tezhm.wax.internal;

import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 *
 */
public class FieldInjectionGenerator
{
    private final String packagePrefix;
    private final XmlWriter writer;

    /**
     *
     * @param packagePrefix
     */
    public FieldInjectionGenerator(String packagePrefix)
    {
        this.packagePrefix = packagePrefix;
        this.writer = new XmlWriter();
    }

    public void process(ClassFieldMap classFieldMap)
    {
        for (Map.Entry<TypeMirror, Set<Element>> classFieldEntry : classFieldMap.entrySet())
        {
            TypeMirror cls = classFieldEntry.getKey();
            String enclosingClassName = cls.toString().replace('.', '/');

            for (Element field : classFieldEntry.getValue())
            {
                String fieldName = field.toString();
                TypeMirror fieldType = field.asType();
                String fieldTypeName = fieldType.toString().replace('.', '/');
                String factoryName = fieldTypeName + "Factory";

                writer.appendField(
                        enclosingClassName,
                        fieldName,
                        fieldTypeName,
                        "com/tezhm/wax/generated/" + factoryName);
            }
        }
    }

    public void flush(Filer filer) throws Exception
    {
        writer.flush(filer);
    }
}
