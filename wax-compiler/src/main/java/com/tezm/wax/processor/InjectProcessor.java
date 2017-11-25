package com.tezm.wax.processor;

import com.tezm.wax.annotation.Inject;
import com.tezm.wax.annotation.Provides;
import com.tezm.wax.factory.FactoryContainer;
import com.tezm.wax.factory.FactoryGenerator;
import com.tezm.wax.injection.InjectionContainer;
import com.tezm.wax.injection.InjectionGenerator;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({"com.tezm.wax.annotation.Inject", "com.tezm.wax.annotation.Provides"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class InjectProcessor extends AbstractProcessor
{
    private static final String generatedFactoryDirectory = "com/tezm/wax/generated/";

    private final FactoryContainer factories;
    private final FactoryGenerator factoryGenerator;
    private final InjectionContainer injections;
    private final InjectionGenerator injectionGenerator;

    public InjectProcessor()
    {
        this(
            new FactoryContainer(),
            new InjectionContainer(),
            new FactoryGenerator(generatedFactoryDirectory),
            new InjectionGenerator(generatedFactoryDirectory)
        );
    }

    public InjectProcessor(
            FactoryContainer factories,
            InjectionContainer injections,
            FactoryGenerator factoryGenerator,
            InjectionGenerator injectionGenerator)
    {
        this.factories = factories;
        this.injections = injections;
        this.factoryGenerator = factoryGenerator;
        this.injectionGenerator = injectionGenerator;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        try
        {
            this.processFields(roundEnv);
            this.processProviders(roundEnv);

            if (roundEnv.processingOver())
            {
                this.factoryGenerator.process(this.factories);
                this.factoryGenerator.flush(processingEnv.getFiler());

                this.injectionGenerator.process(this.injections);
                this.injectionGenerator.flush(processingEnv.getFiler());
            }
        }
        catch (Exception e)
        {
            printErrorMessage(e);
        }

        return true;
    }

    private void processFields(RoundEnvironment roundEnv) throws Exception
    {
        Set<VariableElement> fields =
                ElementFilter.fieldsIn(roundEnv.getElementsAnnotatedWith(Inject.class));

        for (VariableElement field : fields)
        {
            Element parentClass = field.getEnclosingElement();
            TypeMirror classType = parentClass.asType();

            switch (field.getKind())
            {
                case FIELD:
                    this.injections.registerField(classType, field);
                    break;
                case LOCAL_VARIABLE:
                    this.injections.registerVariable(classType, field);
                    break;
                default:
                    // TODO: more information in exception (line number, variable name?)
                    throw new Exception("Unsupported Inject field");
            }

            switch (field.asType().getKind())
            {
                case DECLARED:
                    this.factories.registerFactory(field);
                    break;
                default:
                    // TODO: more information in exception (line number, variable name?)
                    throw new Exception("Unsupported Inject type");
            }
        }
    }

    private void processProviders(RoundEnvironment roundEnv) throws Exception
    {
        Set<ExecutableElement> providers =
                ElementFilter.methodsIn(roundEnv.getElementsAnnotatedWith(Provides.class));

        for (ExecutableElement provider : providers)
        {
            this.factories.registerProvider(provider);
        }
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
}
