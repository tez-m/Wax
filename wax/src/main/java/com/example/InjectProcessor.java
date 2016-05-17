package com.example;

import com.example.xml.Writer;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.example.Inject")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class InjectProcessor extends AbstractProcessor
{
    // Persist during compilation instance
    private static ArrayList<String> classNames = new ArrayList<>();
    private static ArrayList<String> fieldNames = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        Set<TypeElement> parents = ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(Module.class));

        for (TypeElement parent : parents)
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Found class");
            try
            {
                TypeMirror fieldType = parent.asType();
                String fullTypeClassName = fieldType.toString();
                classNames.add(fullTypeClassName);
            }
            catch (Exception e)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, e.toString());
            }
        }

        Set<VariableElement> fields = ElementFilter.fieldsIn(roundEnv.getElementsAnnotatedWith(Inject.class));

        for (VariableElement field : fields)
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Found field");
            try
            {
                TypeMirror fieldType = field.asType();
                String fullTypeClassName = fieldType.toString();
                String message = "Full class name: " + fullTypeClassName;
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
                fieldNames.add(fullTypeClassName);

                MethodSpec make = MethodSpec.methodBuilder("make")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(TypeName.get(fieldType))
                        .addStatement("return new $L()", fullTypeClassName)
                        .build();

                TypeSpec factory = TypeSpec.classBuilder("TestFactory")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(make)
                        .build();

                JavaFile javaFile = JavaFile.builder("com.tez.generated.factory", factory)
                        .build();

                javaFile.writeTo(processingEnv.getFiler());
            }
            catch (Exception e)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, e.toString());
            }
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Class: " + classNames.size());

        for (String name : classNames)
        {
            Writer writer = new Writer();
            writer.AppendClass(name, fieldNames.toArray(new String[fieldNames.size()]));
        }

        return true;
    }
}
