package com.tezhm.wax.annotation.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tezhm.wax.annotation.Inject;
import com.tezhm.wax.internal.XmlWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

@SupportedAnnotationTypes("com.tezhm.wax.annotation.Inject")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class InjectProcessor extends AbstractProcessor
{
    // Persists during compilation instance
    private static final XmlWriter writer = new XmlWriter();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        Set<VariableElement> fields =
                ElementFilter.fieldsIn(roundEnv.getElementsAnnotatedWith(Inject.class));

        for (VariableElement field : fields)
        {
            try
            {
                String enclosingClassName = getEnclosingClassName(field).replace('.', '/');
                String fieldName = getFieldName(field);
                String fieldTypeName = getFieldTypeName(field).replace('.', '/');
                // TODO: Potential file name length issue
                String factoryName = fieldTypeName.replace('/', '_') + "Factory";

                writer.appendField(
                        enclosingClassName,
                        fieldName,
                        fieldTypeName,
                        "com/tezhm/generated/factory/" + factoryName);
                writer.flush(processingEnv.getFiler());

                generateFactory(field, factoryName);
            }
            catch (Exception e)
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String stackTrace = sw.toString();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, stackTrace);
            }
        }

        return true;
    }

    private String getEnclosingClassName(Element field)
    {
        Element parentClass = field.getEnclosingElement();
        TypeMirror classType = parentClass.asType();
        return classType.toString();
    }

    private String getFieldName(Element field)
    {
        return field.toString();
    }

    private String getFieldTypeName(Element field)
    {
        TypeMirror fieldType = field.asType();
        return fieldType.toString();
    }

    private void generateFactory(Element field, String factoryName) throws IOException
    {
        TypeMirror fieldType = field.asType();
        String fieldTypeName = getFieldTypeName(field);

        MethodSpec make = MethodSpec.methodBuilder("make")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.get(fieldType))
                .addStatement("return new $L()", fieldTypeName)
                .build();

        TypeSpec factory = TypeSpec.classBuilder(factoryName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(make)
                .build();

        JavaFile javaFile = JavaFile.builder("com.tezhm.generated.factory", factory)
                .build();

        javaFile.writeTo(processingEnv.getFiler());
    }
}
