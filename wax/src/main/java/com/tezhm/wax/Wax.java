package com.tezhm.wax;

import com.tezhm.wax.internal.FieldTuple;
import com.tezhm.wax.internal.XmlReader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.xml.sax.XMLReader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

class MethodFactoryInjector extends MethodVisitor
{
    private final String cls;
    private final List<FieldTuple> fields;

    /**
     *
     * @param api
     * @param mv
     * @param cls    "com/example/tez_desktop/myapplication/Injectable"
     * @param fields
     */
    public MethodFactoryInjector(int api, MethodVisitor mv, String cls, List<FieldTuple> fields)
    {
        super(api, mv);
        this.cls = cls;
        this.fields = fields;
    }

    //This is the point we insert the code. Note that the instructions are added right after
    //the visitCode method of the super class. This ordering is very important.
    @Override
    public void visitCode()
    {
        super.visitCode();

        for (FieldTuple field : this.fields)
        {
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    field.factoryName,              // Class containing static method
                    "make",                         // Method to statically call
                    "()L" + field.fieldType + ";",  // Type returned by factory
                    false);                         // If it's an interface
            super.visitFieldInsn(
                    Opcodes.PUTFIELD,
                    this.cls,                       // Parent class which holds field
                    field.fieldName,                // Field name which will hold injected value
                    "L" + field.fieldType + ";");   // Type to inject
        }
    }
}

//Our class modifier class visitor. It delegate all calls to the super class
//Only makes sure that it returns our MethodVisitor for every method
class ClassFactoryInjector extends ClassVisitor
{
    private final int api;
    private final String cls;
    private final List<FieldTuple> fields;

    public ClassFactoryInjector(int api, ClassWriter cv, String cls, List<FieldTuple> fields)
    {
        super(api, cv);
        this.api = api;
        this.cls = cls;
        this.fields = fields;
    }

    @Override
    public MethodVisitor visitMethod(
            int access,
            String name,
            String desc,
            String signature,
            String[] exceptions)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if ("<init>".equals(name))
        {
            MethodFactoryInjector mvw = new MethodFactoryInjector(
                    this.api,
                    mv,
                    this.cls,
                    this.fields);
            return mvw;
        }

        return mv;
    }
}

/**
 *
 */
public class Wax
{
    public static void main(String[] args) throws Exception
    {
        try
        {
            File input = new File(args[0] + "/output123.xml");
            XmlReader reader = new XmlReader();
            reader.parse(input);

            for (Map.Entry<String, List<FieldTuple>> a : reader.getClassMap().entrySet())
            {
                String className = a.getKey();
                String classPath = "/" + className + ".class";

                InputStream in = Wax.class.getResourceAsStream(classPath);
                ClassReader classReader = new ClassReader(in);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

                //Wrap the ClassWriter with our custom ClassVisitor
                ClassFactoryInjector mcw = new ClassFactoryInjector(
                        Opcodes.ASM4,
                        cw,
                        className,
                        a.getValue());
                classReader.accept(mcw, 0);

                //Write the output to a class file
                File outputClass = new File(args[0] + classPath);
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
