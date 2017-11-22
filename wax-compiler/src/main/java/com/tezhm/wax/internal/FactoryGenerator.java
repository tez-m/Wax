package com.tezhm.wax.internal;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Filer;
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

    /**
     *
     * @param factories
     * @throws IOException
     */
    public void process(FactorySet factories)
    {
        for (TypeMirror factory : factories)
        {
            String fieldTypeName = factory.toString();
            String packagePath = this.packagePrefix.replace('/', '.') + getPackagePath(fieldTypeName);
            String factoryName = getClassName(fieldTypeName) + "Factory";

            MethodSpec make = MethodSpec.methodBuilder("make")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.get(factory))
                    .addStatement("return new $L()", fieldTypeName)
                    .build();

            TypeSpec factoryClass = TypeSpec.classBuilder(factoryName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(make)
                    .build();

            JavaFile factoryFile = JavaFile.builder(packagePath, factoryClass)
                    .build();

            this.processedFactories.add(factoryFile);

            //throw new RuntimeException(packagePath);
        }


//        for (VariableElement field : fields)
//        {
//            try
//            {
//                String enclosingClassName = getEnclosingClassName(field).replace('.', '/');
//                String fieldName = getFieldName(field);
//                String fieldTypeName = getFieldTypeName(field).replace('.', '/');
//                // TODO: Potential file name length issue
//                String factoryName = fieldTypeName.replace('/', '_') + "Factory";
//
//                writer.appendField(
//                        enclosingClassName,
//                        fieldName,
//                        fieldTypeName,
//                        "com/tezhm/wax/generated/" + factoryName);
//
//                generateFactory(field, factoryName);
//            }
//            catch (Exception e)
//            {
//                printErrorMessage(e);
//            }
//        }


//        TypeMirror fieldType = field.asType();
//        String fieldTypeName = getFieldTypeName(field);
//
//        MethodSpec make = MethodSpec.methodBuilder("make")
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .returns(TypeName.get(fieldType))
//                .addStatement("return new $L()", fieldTypeName)
//                .build();
//
//        TypeSpec factory = TypeSpec.classBuilder(factoryName)
//                .addModifiers(Modifier.PUBLIC)
//                .addMethod(make)
//                .build();
//
//        JavaFile javaFile = JavaFile.builder("com.tezhm.wax.generated", factory)
//                .build();
//
//        javaFile.writeTo(processingEnv.getFiler());
//        factories.add(factoryName);
    }

    public void flush(Filer filer) throws IOException
    {
        for (JavaFile processedFactory : this.processedFactories)
        {
            processedFactory.writeTo(filer);
        }
    }
}
