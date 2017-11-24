package com.tezhm.wax.factory;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

/**
 * Generates factory .class files.
 */
public class FactoryGenerator
{
    private final String packagePrefix;
    private final Set<JavaFile> processedFactories;

    /**
     *
     * @param packagePrefix "com/tezhm/wax/generated/"
     */
    public FactoryGenerator(String packagePrefix)
    {
        this.packagePrefix = packagePrefix;
        this.processedFactories = new HashSet<>();
    }

    /**
     *
     * @param factoryMap
     * @throws IOException
     */
    public void process(FactoryContainer factoryMap)
    {
        for (Map.Entry<String, TypeMirror> factory : factoryMap.factories.entrySet())
        {
            JavaFile factoryFile = createTransientFactory(factory.getKey(), factory.getValue());
            this.processedFactories.add(factoryFile);
        }

        for (Map.Entry<String, ExecutableElement> provider : factoryMap.providers.entrySet())
        {
            JavaFile factoryFile = createProviderFactory(provider.getKey(), provider.getValue());
            this.processedFactories.add(factoryFile);
        }
    }

    private JavaFile createTransientFactory(String factoryType, TypeMirror factoryClass)
    {
        String packagePath = this.packagePrefix.replace('/', '.') + getPackagePath(factoryType);
        String factoryName = getClassName(factoryType) + "Factory";

        MethodSpec make = MethodSpec.methodBuilder("make")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.get(factoryClass))
                .addStatement("return new $T()", TypeName.get(factoryClass))
                .build();

        TypeSpec factory = TypeSpec.classBuilder(factoryName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(make)
                .build();

        return JavaFile.builder(packagePath, factory).build();
    }

    private JavaFile createProviderFactory(String factoryType, ExecutableElement providerMethod)
    {
        String packagePath = this.packagePrefix.replace('/', '.') + getPackagePath(factoryType);
        String factoryName = getClassName(factoryType) + "Factory";

        Element parentClass = providerMethod.getEnclosingElement();
        TypeMirror providerClass = parentClass.asType();

        TypeName returnType = TypeName.get(providerClass);
        FieldSpec provider = FieldSpec.builder(returnType, "provider")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .build();

        MethodSpec make = MethodSpec.methodBuilder("make")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get(getPackagePath(factoryType), getClassName(factoryType)))
                .beginControlFlow("if (provider == null)")
                .addStatement("provider = new $T()", TypeName.get(providerClass))
                .endControlFlow()
                .addStatement("return provider.$L", providerMethod.toString())
                .build();

        TypeSpec factory = TypeSpec.classBuilder(factoryName)
                .addModifiers(Modifier.PUBLIC)
                .addField(provider)
                .addMethod(make)
                .build();

        return JavaFile.builder(packagePath, factory).build();
    }

    public void flush(Filer filer) throws IOException
    {
        for (JavaFile processedFactory : this.processedFactories)
        {
            processedFactory.writeTo(filer);
        }
    }

    private String getPackagePath(String type)
    {
        int lastSlash = type.lastIndexOf('.');

        if (lastSlash == -1)
        {
            return "";
        }

        return type.substring(0, lastSlash);
    }

    private String getClassName(String type)
    {
        int lastSlash = type.lastIndexOf('.');

        if (lastSlash == -1)
        {
            return type;
        }

        return type.substring(lastSlash + 1, type.length());
    }
}
