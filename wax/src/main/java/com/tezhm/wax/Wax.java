package com.tezhm.wax;

import com.tezhm.wax.asm.ClassFactoryInjector;
import com.tezhm.wax.internal.InjectionField;
import com.tezhm.wax.internal.XmlReader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Wax
{
    public static void main(String[] args) throws Exception
    {
        try
        {
            String outputDir = args[0];
            String aptDir = args[1] + "/generated/source/apt/debug/com/tezhm/wax/generated/res/injectionmap.xml";

            File input = new File(aptDir);
            XmlReader reader = new XmlReader();

//            if (args.length > 1)
//            {
//                throw new RuntimeException(Arrays.toString(args));
//            }

            reader.parse(input);

            // TODO: check if class has already been modified
            // TODO: Some files may not be re-compiled and will get injected multiple times
            for (Map.Entry<String, List<InjectionField>> a : reader.getClassMap().entrySet())
            {
                String className = a.getKey();
                String classPath = "/" + className + ".class";

                InputStream in = Wax.class.getResourceAsStream(classPath);
                ClassReader classReader = new ClassReader(in);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

                // Wrap the ClassWriter with our custom ClassVisitor
                ClassFactoryInjector mcw = new ClassFactoryInjector(
                        Opcodes.ASM4,
                        cw,
                        className,
                        a.getValue());
                classReader.accept(mcw, 0);

                // Write the output to a class file
                File outputClass = new File(outputDir + classPath);
                DataOutputStream dout = new DataOutputStream(new FileOutputStream(outputClass));
                dout.write(cw.toByteArray());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
