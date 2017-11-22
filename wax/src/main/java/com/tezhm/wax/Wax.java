package com.tezhm.wax;

import com.tezhm.wax.asm.ClassFactoryInjector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;

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
            String aptDir = args[1] + "/generated/source/apt/debug/com/tezhm/wax/generated/res/injectionmap.json";

            FileReader input = new FileReader(aptDir);
            JSONParser parser = new JSONParser();
            JSONObject resource = (JSONObject) parser.parse(input);
            JSONArray injectionMap = (JSONArray) resource.get("injection_map");

            // TODO: check if class has already been modified
            // TODO: Some files may not be re-compiled and will get injected multiple times
            for (Object entry : injectionMap)
            {
                JSONObject injection = (JSONObject) entry;

                String className = (String) injection.get("class");
                String classPath = "/" + className + ".class";

                InputStream in = Wax.class.getResourceAsStream(classPath);
                ClassReader classReader = new ClassReader(in);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

                // Wrap the ClassWriter with our custom ClassVisitor
                ClassFactoryInjector mcw = new ClassFactoryInjector(
                        Opcodes.ASM4,
                        cw,
                        className,
                        (JSONArray) injection.get("fields")
                );
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
