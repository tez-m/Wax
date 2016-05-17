package com.example;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class MethodFactoryInjector extends MethodVisitor
{
    public MethodFactoryInjector(int api, MethodVisitor mv)
    {
        super(api, mv);
    }

    //This is the point we insert the code. Note that the instructions are added right after
    //the visitCode method of the super class. This ordering is very important.
    @Override
    public void visitCode()
    {
        super.visitCode();
        String type = "com/example/tez_desktop/myapplication/TestInject";
        super.visitVarInsn(Opcodes.ALOAD, 0);
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/tez/generated/factory/TestFactory",    // Class containing static method
                "make",                                     // Method to statically call
                "()L" + type + ";",                         // Type returned by factory
                false);                                     // If it's an interface
        super.visitFieldInsn(
                Opcodes.PUTFIELD,
                "com/example/tez_desktop/myapplication/Injectable", // Parent class which holds field
                "test",             // Field name which will hold injected value
                "L" + type + ";");  // Type to inject
    }
}

//Our class modifier class visitor. It delegate all calls to the super class
//Only makes sure that it returns our MethodVisitor for every method
class ClassFactoryInjector extends ClassVisitor
{
    private int api;

    public ClassFactoryInjector(int api, ClassWriter cv)
    {
        super(api, cv);
        this.api = api;
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
            MethodFactoryInjector mvw = new MethodFactoryInjector(this.api, mv);
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
    public static void main(String[] args) throws IOException
    {
        try
        {
            String classToEdit = "/com/example/tez_desktop/myapplication/Injectable.class";
            InputStream in = Wax.class.getResourceAsStream(classToEdit);
            ClassReader classReader = new ClassReader(in);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            //Wrap the ClassWriter with our custom ClassVisitor
            ClassFactoryInjector mcw = new ClassFactoryInjector(Opcodes.ASM4, cw);
            classReader.accept(mcw, 0);

            //Write the output to a class file
            File outputDir = new File(args[0] + "/com/example/tez_desktop/myapplication/");
            DataOutputStream dout = new DataOutputStream(new FileOutputStream(new File(outputDir, "Injectable.class")));
            dout.write(cw.toByteArray());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
