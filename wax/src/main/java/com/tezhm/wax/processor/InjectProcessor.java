package com.tezhm.wax.processor;

import com.tezhm.wax.annotation.Inject;
import com.tezhm.wax.internal.ClassFieldMap;
import com.tezhm.wax.internal.FactoryGenerator;
import com.tezhm.wax.internal.FactorySet;
import com.tezhm.wax.internal.FieldInjectionGenerator;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.tezhm.wax.annotation.Inject")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class InjectProcessor extends AbstractProcessor
{
    private static final String generatedFactoryDirectory = "com/tezhm/wax/generated/";

    private final FactorySet factorySet;
    private final ClassFieldMap classFieldMap;
    private final FactoryGenerator factoryGenerator;
    private final FieldInjectionGenerator fieldInjectionGenerator;

    public InjectProcessor()
    {
        this(
                new FactorySet(),
                new ClassFieldMap(),
                new FactoryGenerator(generatedFactoryDirectory),
                new FieldInjectionGenerator(generatedFactoryDirectory)
        );
    }

    public InjectProcessor(
            FactorySet factorySet,
            ClassFieldMap classFieldMap,
            FactoryGenerator factoryGenerator,
            FieldInjectionGenerator fieldInjectionGenerator)
    {
        this.factorySet = factorySet;
        this.classFieldMap = classFieldMap;
        this.factoryGenerator = factoryGenerator;
        this.fieldInjectionGenerator = fieldInjectionGenerator;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        Set<VariableElement> fields =
                ElementFilter.fieldsIn(roundEnv.getElementsAnnotatedWith(Inject.class));

        for (VariableElement field : fields)
        {
            try
            {
//                String enclosingClassName = getEnclosingClassName(field).replace('.', '/');
//                String fieldName = getFieldName(field);
//                String fieldTypeName = getFieldTypeName(field).replace('.', '/');
//                // TODO: Potential file name length issue
//                String factoryName = fieldTypeName.replace('/', '_') + "Factory";

//                writer.appendField(
//                        enclosingClassName,
//                        fieldName,
//                        fieldTypeName,
//                        "com/tezhm/wax/generated/" + factoryName);
//
//                generateFactory(field, factoryName);




                Element parentClass = field.getEnclosingElement();
                TypeMirror classType = parentClass.asType();
                TypeMirror fieldType = field.asType();
                this.classFieldMap.registerField(classType, field);
                this.factorySet.register(fieldType);
            }
            catch (Exception e)
            {
                printErrorMessage(e);
            }
        }

        if (roundEnv.processingOver())
        {
            try
            {
                // writer.flush(processingEnv.getFiler());

                this.factoryGenerator.process(this.factorySet);
                this.fieldInjectionGenerator.process(this.classFieldMap);
                this.factoryGenerator.flush(processingEnv.getFiler());
                this.fieldInjectionGenerator.flush(processingEnv.getFiler());
            }
            catch (Exception e)
            {
                printErrorMessage(e);
            }
        }

        return true;
    }

    private void printErrorMessage(Exception e)
    {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        StackTraceElement[] stackTraceElements = e.getStackTrace();

        for (StackTraceElement element : stackTraceElements)
        {
            try
            {
                String message = "Line: " + element.getLineNumber() + " " + element.getClassName() + " " + element.getFileName();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
            }
            catch (Exception ignored) { }
        }
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
}
